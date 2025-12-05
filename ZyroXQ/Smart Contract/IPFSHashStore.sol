pragma solidity ^0.8.19;

contract IPFSHashStore {

   
    string[] public hashList;
    event HashAdded(uint256 index, string cid);
    event HashUpdated(uint256 index, string oldCid, string newCid);
    event HashRemoved(uint256 index, string removedCid);

    function addHash(string memory cid) public {
        hashList.push(cid);
        emit HashAdded(hashList.length - 1, cid);
    }

    function addHashBatch(string[] memory cids) public {
        for (uint256 i = 0; i < cids.length; i++) {
            hashList.push(cids[i]);
            emit HashAdded(hashList.length - 1, cids[i]);
        }
    }

   
    function updateHash(uint256 index, string memory newCid) public {
        require(index < hashList.length, "Invalid index");
        string memory old = hashList[index];
        hashList[index] = newCid;
        emit HashUpdated(index, old, newCid);
    }

   
    function removeHash(uint256 index) public {
        require(index < hashList.length, "Invalid index");

        string memory removedCid = hashList[index];

        hashList[index] = hashList[hashList.length - 1];
        hashList.pop();

        emit HashRemoved(index, removedCid);
    }

    function totalHashes() public view returns (uint256) {
        return hashList.length;
    }

    function getAllHashes() public view returns (string[] memory) {
        return hashList;
    }
}
