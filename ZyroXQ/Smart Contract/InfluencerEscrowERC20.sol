// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

interface IERC20 {
    function transfer(address to, uint256 amount) external;
    function transferFrom(address from, address to, uint256 amount) external;
}

contract InfluencerEscrowERC20 {

    struct Campaign {
        address advertiser;
        address token;
        uint256 totalBudget;
        uint256 remainingBudget;
        uint256 startTime;
        uint256 endTime;
        uint256 costPerView;
        uint256 maxPayoutPerInfluencer;
        bool active;
    }

    struct Influencer {
        bool eligible;
        uint256 views;
        uint256 paidAmount;
    }

    address public owner;
    address public aiVerifier;

    mapping(bytes32 => Campaign) public campaigns;
    mapping(bytes32 => mapping(address => Influencer)) public influencers;

    event CampaignCreated(bytes32 campaignId, address advertiser);
    event InfluencerVerified(bytes32 campaignId, address influencer);
    event InfluencerPaid(bytes32 campaignId, address influencer, uint256 amount);
    event RemainingFundsReleased(bytes32 campaignId, uint256 amount);

    modifier onlyOwner() {
        require(msg.sender == owner, "Not owner");
        _;
    }

    modifier onlyVerifier() {
        require(msg.sender == aiVerifier, "Not verifier");
        _;
    }

    modifier campaignExists(bytes32 campaignId) {
        require(campaigns[campaignId].advertiser != address(0), "Invalid campaign");
        _;
    }

    constructor(address _aiVerifier) {
        require(_aiVerifier != address(0), "Invalid verifier");
        owner = msg.sender;
        aiVerifier = _aiVerifier;
    }

    function createCampaign(
        address token,
        uint256 budget,
        uint256 durationDays,
        uint256 costPerView,
        uint256 maxPayoutPerInfluencer
    ) external returns (bytes32) {

        require(budget > 0, "Budget required");

        IERC20(token).transferFrom(
            msg.sender,
            address(this),
            budget
        );

        bytes32 campaignId = keccak256(
            abi.encodePacked(msg.sender, block.timestamp, budget)
        );

        campaigns[campaignId] = Campaign({
            advertiser: msg.sender,
            token: token,
            totalBudget: budget,
            remainingBudget: budget,
            startTime: block.timestamp,
            endTime: block.timestamp + (durationDays * 1 days),
            costPerView: costPerView,
            maxPayoutPerInfluencer: maxPayoutPerInfluencer,
            active: true
        });

        emit CampaignCreated(campaignId, msg.sender);
        return campaignId;
    }

    function verifyInfluencer(
        bytes32 campaignId,
        address influencer,
        uint256 views,
        bool eligible
    ) external onlyVerifier campaignExists(campaignId) {

        influencers[campaignId][influencer].views = views;
        influencers[campaignId][influencer].eligible = eligible;

        emit InfluencerVerified(campaignId, influencer);
    }

    function releaseInfluencerPayment(
        bytes32 campaignId,
        address influencer
    ) external campaignExists(campaignId) {

        Campaign storage c = campaigns[campaignId];
        Influencer storage i = influencers[campaignId][influencer];

        require(c.active, "Campaign inactive");
        require(i.eligible, "Not eligible");

        uint256 earned = i.views * c.costPerView;
        if (earned > c.maxPayoutPerInfluencer) {
            earned = c.maxPayoutPerInfluencer;
        }

        uint256 payableAmount = earned - i.paidAmount;
        require(payableAmount > 0, "Nothing to pay");

        i.paidAmount += payableAmount;
        c.remainingBudget -= payableAmount;

        IERC20(c.token).transfer(influencer, payableAmount);

        emit InfluencerPaid(campaignId, influencer, payableAmount);
    }

    function releaseRemainingFunds(bytes32 campaignId)
        external
        campaignExists(campaignId)
    {
        Campaign storage c = campaigns[campaignId];
        require(block.timestamp > c.endTime, "Campaign running");

        c.active = false;
        uint256 refund = c.remainingBudget;
        c.remainingBudget = 0;

        IERC20(c.token).transfer(c.advertiser, refund);
        emit RemainingFundsReleased(campaignId, refund);
    }
}
