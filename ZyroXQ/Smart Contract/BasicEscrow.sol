// SPDX-License-Identifier: MIT

pragma solidity ^0.8.25;

contract BasicEscrow {

    // State Variables
    // --------------------
    address public buyer;
    address public seller;
    uint256 public amountDeposited; // Amount of ETH deposited into the escrow
    uint256 public depositTimestamp; // Timestamp of the deposit

    // Current state of the escrow contract
    enum State { Created, Locked, Released, Refunded }
    State public currentState;

    // --------------------
    // Events
    // --------------------
    event Deposited(address indexed buyer, uint256 amount);
    event Released(address indexed seller, uint256 amount);
    event Refunded(address indexed buyer, uint256 amount);

    // --------------------
    // Constructor
    // --------------------
        /**
     * @dev Initializes the contract with seller address.
     *      The buyer is set as the deployer of the contract.
     *      Initial state is set to Created.
     */ 
    constructor(address _seller) {
        buyer = msg.sender;
        seller = _seller;
        currentState = State.Created;
    }

    // --------------------
    // Modifiers
    // --------------------
    /**
     * @dev Restricts function access to the buyer only.
     */
    modifier onlyBuyer() {
        require(msg.sender == buyer, "Only buyer can call this function");
        _;
    }
    /**
     * @dev Ensures that the contract is in a specific state before proceeding.
     * @param expectedState The state that the contract must be in.
     */
    modifier inState(State expectedState) {
        require (currentState == expectedState, "Invalid state for this operation");
        _;
    }

    // --------------------
    // Function Stubs for Next Steps
    // --------------------

    /**
     * @dev Allows the buyer to deposit funds into the escrow.
     *      The contract state changes to Locked after deposit.
     */
    function deposit() external payable onlyBuyer inState(State.Created) {
        require(msg.value > 0, "Deposit amount must be greater than zero"); 

        amountDeposited += msg.value; // Update the amount deposited
        depositTimestamp = block.timestamp; // Set the timestamp of deposit
        currentState = State.Locked; // Change state to Locked
        
        emit Deposited(buyer, msg.value); // Emit the Deposited event
    }
    
    /**
     * @dev Allows the buyer to release the funds to the seller.
     *      The contract state changes to Released after funds are released.
     */
    function releaseFunds() external onlyBuyer inState(State.Locked) {
        require(amountDeposited > 0, "No funds to release"); // Ensure there are funds to release

        currentState = State.Released; // Change state to Released
        uint256 amount = amountDeposited; // Store the amount to be released
        amountDeposited = 0; // Reset the deposited amount

        (bool success, ) = seller.call{value: amount}(""); // Transfer funds to the seller
        require(success, "Transfer failed."); // Ensure the transfer was successful
        
        emit Released(seller, amount); // Emit the Released event   
    }

    /**
     * @dev Internal function to handle refunds.
     *      Internal state used to add cancelWithTimeout functionality.
     *      This function is called when the buyer requests a refund or cancels the escrow.
     */
    function _refund() internal {
        require(amountDeposited > 0, "No funds to refund"); // Ensure there are funds to refund

        currentState = State.Refunded; // Change state to Refunded
        uint256 amount = amountDeposited; // Store the amount to be refunded
        amountDeposited = 0; // Reset the deposited amount

        (bool success, ) = buyer.call{value: amount}(""); // Transfer funds to the buyer
        require(success, "Transfer failed"); // Ensure the transfer was successful
        emit Refunded(buyer, amount); // Emit the Refunded event
    }
    /**
     * @dev Allows the buyer to request a refund externally.
     *      The contract state changes to Refunded after the refund is processed.
     */
    function refund() external onlyBuyer inState(State.Locked) {
        _refund(); // Use the internal helper to avoid repeating logic
    }
    /**
     * @dev Allows the buyer to cancel the escrow after a timeout period.
     *      The contract state changes to Refunded after cancellation.
     */
    function cancelWithTimeout() external onlyBuyer inState(State.Locked) {
        require(block.timestamp > depositTimestamp + 30 days, "Cannot cancel before timeout"); // Ensure the timeout has passed
        _refund(); // Call the refund function to return funds to the buyer
    }

}