// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title Escrow Contract
 * @dev A secure and transparent escrow system suitable for influencer–advertiser workflows.
 * Funds are locked until advertiser approves or admin resolves disputes.
 */
contract Escrow {
    
    enum EscrowStatus { 
        NOT_CREATED,
        FUNDED,
        APPROVED,
        RELEASED,
        REFUNDED,
        DISPUTED 
    }

    struct Deal {
        address advertiser;     // Who deposits funds (payer)
        address influencer;     // Who receives funds (payee)
        uint256 amount;         // Locked payment amount
        EscrowStatus status;    // Current state
    }

    address public admin; // Escrow arbitrator
    uint256 public dealCounter;
    mapping(uint256 => Deal) public deals;

    // ------------------ EVENTS ---------------------
    event DealCreated(uint256 dealId, address advertiser, address influencer, uint256 amount);
    event DealApproved(uint256 dealId);
    event FundsReleased(uint256 dealId, uint256 amount);
    event FundsRefunded(uint256 dealId, uint256 amount);
    event DealDisputed(uint256 dealId);

    constructor() {
        admin = msg.sender;  
    }

    // ------------------ MODIFIERS -------------------
    modifier onlyAdvertiser(uint256 _dealId) {
        require(deals[_dealId].advertiser == msg.sender, "Not advertiser");
        _;
    }

    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this");
        _;
    }

    // ------------------ CORE ESCROW LOGIC ---------------------

    /**
     * @notice Create a new escrow deal & lock funds
     */
    function createDeal(address _influencer) external payable returns (uint256) {
        require(msg.value > 0, "Amount must be > 0");
        require(_influencer != address(0), "Invalid influencer address");

        dealCounter++;
        deals[dealCounter] = Deal({
            advertiser: msg.sender,
            influencer: _influencer,
            amount: msg.value,
            status: EscrowStatus.FUNDED
        });

        emit DealCreated(dealCounter, msg.sender, _influencer, msg.value);
        return dealCounter;
    }

    /**
     * @notice Advertiser approves influencer's delivery
     */
    function approveWork(uint256 _dealId) external onlyAdvertiser(_dealId) {
        Deal storage d = deals[_dealId];
        require(d.status == EscrowStatus.FUNDED, "Deal not funded");

        d.status = EscrowStatus.APPROVED;
        emit DealApproved(_dealId);
    }

    /**
     * @notice Release funds to influencer after approval
     */
    function releaseFunds(uint256 _dealId) external {
        Deal storage d = deals[_dealId];
        require(
            msg.sender == d.advertiser || msg.sender == admin,
            "Not allowed"
        );
        require(
            d.status == EscrowStatus.APPROVED,
            "Work not approved"
        );

        uint256 amount = d.amount;
        d.amount = 0;
        d.status = EscrowStatus.RELEASED;

        payable(d.influencer).transfer(amount);
        emit FundsReleased(_dealId, amount);
    }

    /**
     * @notice Advertiser opens dispute (if unhappy with delivery)
     */
    function raiseDispute(uint256 _dealId) external onlyAdvertiser(_dealId) {
        Deal storage d = deals[_dealId];
        require(d.status == EscrowStatus.FUNDED, "Cannot dispute");

        d.status = EscrowStatus.DISPUTED;
        emit DealDisputed(_dealId);
    }

    /**
     * @notice Admin resolves dispute by refunding advertiser
     */
    function refundAdvertiser(uint256 _dealId) external onlyAdmin {
        Deal storage d = deals[_dealId];
        require(d.status == EscrowStatus.DISPUTED, "Not disputed");

        uint256 amount = d.amount;
        d.amount = 0;
        d.status = EscrowStatus.REFUNDED;

        payable(d.advertiser).transfer(amount);
        emit FundsRefunded(_dealId, amount);
    }

    /**
     * @notice Admin resolves dispute by releasing payment
     */
    function adminRelease(uint256 _dealId) external onlyAdmin {
        Deal storage d = deals[_dealId];
        require(d.status == EscrowStatus.DISPUTED, "Not in dispute");

        uint256 amount = d.amount;
        d.amount = 0;
        d.status = EscrowStatus.RELEASED;

        payable(d.influencer).transfer(amount);
        emit FundsReleased(_dealId, amount);
    }
}
