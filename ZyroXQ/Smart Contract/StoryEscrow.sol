// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

contract StoryEscrow is ReentrancyGuard {

    /* ========== ENUMS ========== */

    enum CampaignStatus { OPEN, EXPIRED }
    enum StoryStatus { SUBMITTED, APPROVED, REJECTED }

    /* ========== STRUCTS ========== */

    struct Campaign {
        uint256 campaignId;
        address advertiser;
        string companyName;
        uint256 budget;            // total ETH deposited
        uint256 payPerInfluencer;  // payout per approval (wei)
        uint256 remainingFund;
        uint256 expiryDate;
        CampaignStatus status;
        uint256 createdAt;
    }

    struct StorySubmission {
        address influencer;
        string storyUrl;
        StoryStatus status;
        uint256 submittedAt;
    }

    /* ========== STATE VARIABLES ========== */

    uint256 public totalCampaigns;

    mapping(uint256 => Campaign) public campaigns;
    mapping(uint256 => StorySubmission[]) private submissions;
    mapping(uint256 => mapping(address => bool)) public hasSubmitted;

    /* ========== EVENTS ========== */

    event CampaignCreated(
        uint256 indexed campaignId,
        address indexed advertiser,
        uint256 budget,
        uint256 expiryDate
    );

    event StorySubmitted(
        uint256 indexed campaignId,
        address indexed influencer,
        string storyUrl
    );

    event StoryApproved(
        uint256 indexed campaignId,
        address indexed influencer,
        uint256 payout
    );

    event StoryRejected(
        uint256 indexed campaignId,
        address indexed influencer
    );

    event CampaignExpired(uint256 indexed campaignId);
    event FundRefunded(uint256 indexed campaignId, uint256 amount);

    /* ========== ADVERTISER FUNCTIONS ========== */

    function createCampaign(
        string calldata companyName,
        uint256 expiryDate,
        uint256 payPerInfluencer
    ) external payable returns (uint256) {

        require(msg.value > 0, "Budget required");
        require(payPerInfluencer > 0, "Invalid payout");
        require(payPerInfluencer <= msg.value, "Payout exceeds budget");
        require(expiryDate > block.timestamp, "Invalid expiry");

        totalCampaigns++;

        campaigns[totalCampaigns] = Campaign({
            campaignId: totalCampaigns,
            advertiser: msg.sender,
            companyName: companyName,
            budget: msg.value,
            payPerInfluencer: payPerInfluencer,
            remainingFund: msg.value,
            expiryDate: expiryDate,
            status: CampaignStatus.OPEN,
            createdAt: block.timestamp
        });

        emit CampaignCreated(
            totalCampaigns,
            msg.sender,
            msg.value,
            expiryDate
        );

        return totalCampaigns;
    }

    function reviewStory(
        uint256 campaignId,
        uint256 submissionIndex,
        bool approve
    ) external nonReentrant {

        Campaign storage campaign = campaigns[campaignId];

        require(campaign.campaignId != 0, "Invalid campaign");
        require(msg.sender == campaign.advertiser, "Only advertiser");
        require(campaign.status == CampaignStatus.OPEN, "Campaign expired");

        require(
            submissionIndex < submissions[campaignId].length,
            "Invalid submission index"
        );

        StorySubmission storage story =
            submissions[campaignId][submissionIndex];

        require(story.status == StoryStatus.SUBMITTED, "Already reviewed");

        if (approve) {
            require(
                campaign.remainingFund >= campaign.payPerInfluencer,
                "Insufficient fund"
            );

            campaign.remainingFund -= campaign.payPerInfluencer;
            story.status = StoryStatus.APPROVED;

            (bool success, ) =
                payable(story.influencer).call{
                    value: campaign.payPerInfluencer
                }("");

            require(success, "ETH transfer failed");

            emit StoryApproved(
                campaignId,
                story.influencer,
                campaign.payPerInfluencer
            );

        } else {
            story.status = StoryStatus.REJECTED;
            emit StoryRejected(campaignId, story.influencer);
        }
    }

    /* ========== INFLUENCER FUNCTIONS ========== */

    function submitStory(
        uint256 campaignId,
        string calldata storyUrl
    ) external {

        Campaign storage campaign = campaigns[campaignId];

        require(campaign.campaignId != 0, "Invalid campaign");
        require(campaign.status == CampaignStatus.OPEN, "Campaign expired");
        require(block.timestamp <= campaign.expiryDate, "Expired");
        require(!hasSubmitted[campaignId][msg.sender], "Already submitted");
        require(bytes(storyUrl).length > 0, "Invalid URL");

        submissions[campaignId].push(
            StorySubmission({
                influencer: msg.sender,
                storyUrl: storyUrl,
                status: StoryStatus.SUBMITTED,
                submittedAt: block.timestamp
            })
        );

        hasSubmitted[campaignId][msg.sender] = true;

        emit StorySubmitted(campaignId, msg.sender, storyUrl);
    }

    /* ========== EXPIRY LOGIC ========== */

    function expireCampaign(uint256 campaignId) external nonReentrant {

        Campaign storage campaign = campaigns[campaignId];

        require(campaign.campaignId != 0, "Invalid campaign");
        require(campaign.status == CampaignStatus.OPEN, "Already expired");
        require(block.timestamp > campaign.expiryDate, "Not expired yet");

        StorySubmission[] storage list = submissions[campaignId];

        for (uint256 i = 0; i < list.length; i++) {
            if (list[i].status == StoryStatus.SUBMITTED) {
                campaign.status = CampaignStatus.EXPIRED;
                emit CampaignExpired(campaignId);
                return;
            }
        }

        uint256 refund = campaign.remainingFund;
        campaign.remainingFund = 0;
        campaign.status = CampaignStatus.EXPIRED;

        if (refund > 0) {
            (bool success, ) =
                payable(campaign.advertiser).call{value: refund}("");
            require(success, "Refund failed");

            emit FundRefunded(campaignId, refund);
        }

        emit CampaignExpired(campaignId);
    }

    /* ========== VIEW FUNCTIONS ========== */

    function getSubmissions(uint256 campaignId)
        external
        view
        returns (StorySubmission[] memory)
    {
        return submissions[campaignId];
    }
}
