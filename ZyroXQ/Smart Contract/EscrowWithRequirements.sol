    // SPDX-License-Identifier: MIT
    pragma solidity ^0.8.20;

    import "@openzeppelin/contracts/access/Ownable.sol";
    import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";
    import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

    contract EscrowWithRequirements is Ownable, ReentrancyGuard {
        constructor() Ownable(msg.sender) {}  

        enum SubmissionStatus {
            Pending,
            Approved,
            Rejected
        }

        struct Campaign {
            address advertiser;
            address token; // ERC20 or address(0) for native ETH
            uint256 budget;
            uint256 perInfluencerPayout;
            uint256 maxInfluencers;
            string textRequirements; // freeform (AI-agent will parse)
            uint256 minFollowers;    // hard requirement
            string mustIncludeAsset; // e.g. brand name or hashtag
            uint256 createdAt;
            uint256 deadline;
            bool cancelled;
        }

        struct Submission {
            address influencer;
            string url;
            SubmissionStatus status;
            uint256 verifiedAt;
            uint256 payoutAmount;
        }

        uint256 public campaignCount;
        mapping(uint256 => Campaign) public campaigns;
        mapping(uint256 => Submission[]) public submissions;
        mapping(uint256 => address[]) public waitlists;

        event CampaignCreated(
            uint256 indexed id,
            address advertiser,
            address token,
            uint256 budget,
            uint256 perInfluencerPayout,
            uint256 maxInfluencers,
            string textRequirements,
            uint256 minFollowers,
            string mustIncludeAsset,
            uint256 deadline
        );

        event InfluencerSubmitted(
            uint256 indexed campaignId,
            address indexed influencer,
            uint256 indexed submissionId,
            string url
        );
        event InfluencerWaitlisted(uint256 indexed campaignId, address indexed influencer);
        event SubmissionVerified(uint256 indexed campaignId, uint256 indexed submissionId, bool eligible);
        event Paid(address indexed influencer, uint256 amount);
        event Cancelled(uint256 indexed campaignId);

        modifier onlyAdvertiser(uint256 campaignId) {
            require(campaignId < campaignCount, "Invalid campaign");
            require(msg.sender == campaigns[campaignId].advertiser, "Not advertiser");
            _;
        }

        modifier campaignActive(uint256 campaignId) {
            require(!campaigns[campaignId].cancelled, "Cancelled");
            require(block.timestamp <= campaigns[campaignId].deadline, "Expired");
            _;
        }

        /// @notice Create new campaign
        function createCampaign(
            address token,
            uint256 budget,
            uint256 perInfluencerPayout,
            uint256 maxInfluencers,
            string memory textRequirements,
            uint256 minFollowers,
            string memory mustIncludeAsset,
            uint256 duration
        ) external payable nonReentrant returns (uint256) {
            require(maxInfluencers > 0, "Need at least 1");
            require(budget >= perInfluencerPayout * maxInfluencers, "Budget too small");
            //  require(budget >= 0.5 * 0.01, "Budget too small");

            if (token == address(0)) {
                // Native ETH
                require(msg.value == budget, "ETH not equal to budget");
            } else {
                // ERC20
                IERC20(token).transferFrom(msg.sender, address(this), budget);
            }

            Campaign storage c = campaigns[campaignCount];
            c.advertiser = msg.sender;
            c.token = token;
            c.budget = budget;
            c.perInfluencerPayout = perInfluencerPayout;
            c.maxInfluencers = maxInfluencers;
            c.textRequirements = textRequirements;
            c.minFollowers = minFollowers;
            c.mustIncludeAsset = mustIncludeAsset;
            c.createdAt = block.timestamp;
            c.deadline = block.timestamp + duration;
            c.cancelled = false;

            emit CampaignCreated(
                campaignCount,
                msg.sender,
                token,
                budget,
                perInfluencerPayout,
                maxInfluencers,
                textRequirements,
                minFollowers,
                mustIncludeAsset,
                c.deadline
            );

            campaignCount++;
            return campaignCount - 1;
        }

        /// @notice Influencer submits URL
        function submitInfluencer(
            uint256 campaignId,
            string calldata url
        ) external campaignActive(campaignId) nonReentrant returns (uint256) {
            Campaign storage c = campaigns[campaignId];

            if (submissions[campaignId].length < c.maxInfluencers) {
                Submission memory s = Submission({
                    influencer: msg.sender,
                    url: url,
                    status: SubmissionStatus.Pending,
                    verifiedAt: 0,
                    payoutAmount: c.perInfluencerPayout
                });

                submissions[campaignId].push(s);
                uint256 submissionId = submissions[campaignId].length - 1;
                emit InfluencerSubmitted(campaignId, msg.sender, submissionId, url);
                return submissionId;
            } else {
                waitlists[campaignId].push(msg.sender);
                emit InfluencerWaitlisted(campaignId, msg.sender);
                return type(uint256).max;
            }
        }

        /// @notice Verifier (AI agent or advertiser) checks influencer submission
        function verifySubmission(
            uint256 campaignId,
            uint256 submissionId,
            bool eligible
        ) external nonReentrant {
            require(campaignId < campaignCount, "Invalid campaign");
            require(submissionId < submissions[campaignId].length, "Invalid submission");

            Submission storage s = submissions[campaignId][submissionId];
            require(s.status == SubmissionStatus.Pending, "Already verified");

            if (eligible) {
                s.status = SubmissionStatus.Approved;
                s.verifiedAt = block.timestamp;
                _payInfluencer(campaignId, s.influencer, s.payoutAmount);
            } else {
                s.status = SubmissionStatus.Rejected;
                s.verifiedAt = block.timestamp;
                _tryPromoteFromWaitlist(campaignId);
            }

            emit SubmissionVerified(campaignId, submissionId, eligible);
        }

        function _payInfluencer(uint256 campaignId, address influencer, uint256 amount) internal {
            Campaign storage c = campaigns[campaignId];
            require(c.budget >= amount, "Budget exhausted");

            c.budget -= amount;

            if (c.token == address(0)) {
                (bool ok, ) = influencer.call{value: amount}("");
                require(ok, "ETH transfer failed");
            } else {
                IERC20(c.token).transfer(influencer, amount);
            }

            emit Paid(influencer, amount);
        }

        function _tryPromoteFromWaitlist(uint256 campaignId) internal {
            Campaign storage c = campaigns[campaignId];
            if (waitlists[campaignId].length > 0 && submissions[campaignId].length < c.maxInfluencers) {
                address nextInfluencer = waitlists[campaignId][0];

                // shift array (FIFO)
                for (uint i = 0; i < waitlists[campaignId].length - 1; i++) {
                    waitlists[campaignId][i] = waitlists[campaignId][i + 1];
                }
                waitlists[campaignId].pop();

                Submission memory s = Submission({
                    influencer: nextInfluencer,
                    url: "",
                    status: SubmissionStatus.Pending,
                    verifiedAt: 0,
                    payoutAmount: c.perInfluencerPayout
                });

                submissions[campaignId].push(s);
                emit InfluencerSubmitted(campaignId, nextInfluencer, submissions[campaignId].length - 1, "");
            }
        }

        /// @notice Advertiser cancels campaign and gets refund of remaining budget
        function cancelCampaign(uint256 campaignId) external onlyAdvertiser(campaignId) nonReentrant {
            Campaign storage c = campaigns[campaignId];
            require(!c.cancelled, "Already cancelled");

            c.cancelled = true;
            if (c.budget > 0) {
                if (c.token == address(0)) {
                    (bool ok, ) = c.advertiser.call{value: c.budget}("");
                    require(ok, "ETH refund failed");
                } else {
                    IERC20(c.token).transfer(c.advertiser, c.budget);
                }
                c.budget = 0;
            }

            emit Cancelled(campaignId);
        }
    }
