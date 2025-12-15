// SPDX-License-Identifier: MIT

pragma solidity ^0.8.7;

import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

contract StoryEscrow is ReentrancyGuard {
    address public escAcc;
    uint256 public escBal;
    uint256 public escAvailBal;
    uint256 public escFee;
    uint256 public totalCampaign = 0;
    uint256 public totalConfirmed = 0;
    uint256 public totalDisputed = 0;

    mapping(uint256 => campaignStruct) private campaigns;
    mapping(address => campaignStruct[]) private campaignsOf;
    mapping(address => mapping(uint256 => bool)) public requested;
    mapping(uint256 => address) public ownerOf;
    mapping(uint256 => Available) public isAvailable;

    enum Status {
        OPEN,
        PENDING,
        DELIVERY,
        CONFIRMED,
        DISPUTTED,
        REFUNDED,
        WITHDRAWED
    }

    enum Available { NO, YES }

    struct campaignStruct {
        uint256 campaignId;
        string purpose;
        uint256 amount;
        uint256 timestamp;
        address owner;
        address provider;
        Status status;
        bool provided;
        bool confirmed;
    }

    event Action (
        uint256 campaignId,
        string actionType,
        Status status,
        address indexed executor
    );

    constructor(uint256 _escFee) {
        escAcc = msg.sender;
        escBal = 0;
        escAvailBal = 0;
        escFee = _escFee;
    }

    function createCampaign(
        string calldata purpose
    ) payable external returns (bool) {
        require(bytes(purpose).length > 0, "Purpose cannot be empty");
        require(msg.value > 0 ether, "Item cannot be zero ethers");

        uint256 campaignId = totalCampaign++;
        campaignStruct storage campaign = campaigns[campaignId];

        campaign.campaignId = campaignId;
        campaign.purpose = purpose;
        campaign.amount = msg.value;
       campaign.timestamp = block.timestamp;
        campaign.owner = msg.sender;
        campaign.status = Status.OPEN;

        campaignsOf[msg.sender].push(campaign);
        ownerOf[campaignId] = msg.sender;
        isAvailable[campaignId] = Available.YES;
        escBal += msg.value;

        emit Action (
            campaignId,
            "Campaign CREATED",
            Status.OPEN,
            msg.sender
        );
        return true;
    }

    function getCampaign()
        external
        view
        returns (campaignStruct[] memory props) {
        props = new campaignStruct[](totalCampaign);

        for (uint256 i = 0; i < totalCampaign; i++) {
            props[i] = campaigns[i];
        }
    }

    function getCampaign(uint256 campaignId)
        external
        view
        returns (campaignStruct memory) {
        return campaigns[campaignId];
    }

    function myItems()
        external
        view
        returns (campaignStruct[] memory) {
        return campaignsOf[msg.sender];
    }

    function requestItem(uint256 campaignId) external returns (bool) {
        require(msg.sender != ownerOf[campaignId], "Owner not allowed");
        require(isAvailable[campaignId] == Available.YES, "Campaign not available");

        requested[msg.sender][campaignId] = true;

        emit Action (
            campaignId,
            "REQUESTED",
            Status.OPEN,
            msg.sender
        );

        return true;
    }

    function approveRequest(
        uint256 campaignId,
        address provider
    ) external returns (bool) {
        require(msg.sender == ownerOf[campaignId], "Only owner allowed");
        require(isAvailable[campaignId] == Available.YES, "Campaign not available");
        require(requested[provider][campaignId], "Provider not on the list");

        isAvailable[campaignId] == Available.NO;
        campaigns[campaignId].status = Status.PENDING;
        campaigns[campaignId].provider = provider;

        emit Action (
            campaignId,
            "APPROVED",
            Status.PENDING,
            msg.sender
        );

        return true;
    }

    function performDelievery(uint256 campaignId) external returns (bool) {
        require(msg.sender == campaigns[campaignId].provider, "Service not awarded to you");
        require(!campaigns[campaignId].provided, "Service already provided");
        require(!campaigns[campaignId].confirmed, "Service already confirmed");

        campaigns[campaignId].provided = true;
        campaigns[campaignId].status = Status.DELIVERY;

        emit Action (
            campaignId,
            "DELIVERY INTIATED",
            Status.DELIVERY,
            msg.sender
        );

        return true;
    }

    function confirmDelivery(
        uint256 campaignId,
        bool provided
    ) external returns (bool) {
        require(msg.sender == ownerOf[campaignId], "Only owner allowed");
        require(campaigns[campaignId].provided, "Service not provided");
        require(campaigns[campaignId].status != Status.REFUNDED, "Already refunded, create a new Item");

        if(provided) {
            uint256 fee = (campaigns[campaignId].amount * escFee) / 100;
            payTo(campaigns[campaignId].provider, (campaigns[campaignId].amount - fee));
            escBal -= campaigns[campaignId].amount;
            escAvailBal += fee;

            campaigns[campaignId].confirmed = true;
            campaigns[campaignId].status = Status.CONFIRMED;
            totalConfirmed++;
        }else {
           campaigns[campaignId].status = Status.DISPUTTED; 
        }

        emit Action (
            campaignId,
            "DISPUTTED",
            Status.DISPUTTED,
            msg.sender
        );

        return true;
    }

    function refundItem(uint256 campaignId) external returns (bool) {
        require(msg.sender == escAcc, "Only Escrow allowed");
        require(!campaigns[campaignId].confirmed, "Service already provided");

        payTo(campaigns[campaignId].owner, campaigns[campaignId].amount);
        escBal -= campaigns[campaignId].amount;
        campaigns[campaignId].status = Status.REFUNDED;
        totalDisputed++;

        emit Action (
            campaignId,
            "REFUNDED",
            Status.REFUNDED,
            msg.sender
        );

        return true;
    }

    function withdrawFund(
        address to,
        uint256 amount
    ) external returns (bool) {
        require(msg.sender == escAcc, "Only Escrow allowed");
        require(amount > 0 ether && amount <= escAvailBal, "Zero withdrawal not allowed");

        payTo(to, amount);
        escAvailBal -= amount;

        emit Action (
            block.timestamp,
            "WITHDRAWED",
            Status.WITHDRAWED,
            msg.sender
        );

        return true;
    }

    function payTo(
        address to, 
        uint256 amount
    ) internal returns (bool) {
        (bool success,) = payable(to).call{value: amount}("");
        require(success, "Payment failed");
        return true;
    }
}