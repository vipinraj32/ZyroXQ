// SPDX-License-Identifier: MIT
pragma solidity ^0.8.25;

contract MultiEscrow {

    enum State { Created, Locked, Released, Refunded }

    struct Campaign {
        address buyer;
        address influencer;   // updated naming
        uint256 budget;
        uint256 depositedAmount;
        uint256 depositTimestamp;
        bool influencerVerified;  
        bool buyerEligible;       
        State state;
    }

    mapping(uint256 => Campaign) public campaigns;
    uint256 public nextCampaignId;

    address public admin;     // AI/backend/verifier address

    // Events
    event CampaignCreated(uint256 indexed id, address buyer, address influencer, uint256 budget);
    event Deposited(uint256 indexed id, uint256 amount);
    event Released(uint256 indexed id, uint256 amount);
    event Refunded(uint256 indexed id, uint256 amount);
    event InfluencerVerified(uint256 indexed id);
    event BuyerEligible(uint256 indexed id);

    constructor() {
        admin = msg.sender;
    }

    modifier onlyAdmin() {
        require(msg.sender == admin, "Not authorized");
        _;
    }

    modifier onlyBuyer(uint256 id) {
        require(msg.sender == campaigns[id].buyer, "Not campaign buyer");
        _;
    }

    modifier inState(uint256 id, State expected) {
        require(campaigns[id].state == expected, "Invalid state");
        _;
    }

    // ----------------------
    // CREATE CAMPAIGN
    // ----------------------
    function createCampaign(address _influencer, uint256 _budget) external returns(uint256) {
        require(_budget > 0, "Budget > 0 required");

        uint256 id = nextCampaignId++;

        campaigns[id] = Campaign({
            buyer: msg.sender,
            influencer: _influencer,
            budget: _budget,
            depositedAmount: 0,
            depositTimestamp: 0,
            influencerVerified: false,
            buyerEligible: false,
            state: State.Created
        });

        emit CampaignCreated(id, msg.sender, _influencer, _budget);
        return id;
    }

    // ----------------------
    // DEPOSIT FUNDS
    // ----------------------
    function deposit(uint256 id)
        external
        payable
        onlyBuyer(id)
        inState(id, State.Created)
    {
        Campaign storage c = campaigns[id];
        require(msg.value == c.budget, "Incorrect amount");

        c.depositedAmount = msg.value;
        c.depositTimestamp = block.timestamp;
        c.state = State.Locked;

        emit Deposited(id, msg.value);
    }

    // ----------------------
    // ADMIN: VERIFY INFLUENCER
    // ----------------------
    function markInfluencerVerified(uint256 id)
        external
        onlyAdmin
        inState(id, State.Locked)
    {
        campaigns[id].influencerVerified = true;
        emit InfluencerVerified(id);

        _autoRelease(id);
    }

    // ----------------------
    // ADMIN: CHECK BUYER ELIGIBILITY
    // ----------------------
    function markBuyerEligible(uint256 id)
        external
        onlyAdmin
        inState(id, State.Locked)
    {
        campaigns[id].buyerEligible = true;
        emit BuyerEligible(id);

        _autoRelease(id);
    }

    // ----------------------
    // INTERNAL AUTO RELEASE LOGIC
    // ----------------------
    function _autoRelease(uint256 id) internal {
        Campaign storage c = campaigns[id];

        // REQUIRE BOTH CONDITIONS TO BE TRUE
        if (c.influencerVerified && c.buyerEligible) {
            uint256 amount = c.depositedAmount;

            c.state = State.Released;
            c.depositedAmount = 0;

            (bool ok, ) = c.influencer.call{value: amount}("");
            require(ok, "Payment failed");

            emit Released(id, amount);
        }
    }

    // ----------------------
    // OPTIONAL REFUND
    // ----------------------
    function refund(uint256 id)
        external
        onlyBuyer(id)
        inState(id, State.Locked)
    {
        Campaign storage c = campaigns[id];
        uint256 amount = c.depositedAmount;

        c.state = State.Refunded;
        c.depositedAmount = 0;

        (bool ok, ) = c.buyer.call{value: amount}("");
        require(ok, "Refund failed");

        emit Refunded(id, amount);
    }
}
