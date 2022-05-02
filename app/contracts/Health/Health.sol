// SPDX-License-Identifier: UNLICENSED

pragma solidity >=0.7.0 <0.9.0;
 
contract Health{

   struct EmpExceStruct{
      address addr;
      string info;
      uint startTime;
      uint endTime;
      string exerciseType;
      string extra;
   }
  
   struct Employee {
        bool created;
        string info;
        uint256 exerciseCount;
        mapping(uint => Exercise) et;
        uint etSize;
        // TODO connect to address of user
    
   }
 
   struct Exercise {
        // TODO how to startTime
        uint startTime;
        uint endTime;
        string exerciseType;
        string extra;
 
   }

    mapping(address => Employee) public employees;
    mapping(uint => address) public addresses;
    uint addressMapSize;

    address public admin;
 
    // creates the admin user
    constructor(){
        admin = msg.sender;  
    }
    
    // Creates an employee with an initial health transaction with no data
    // https://stackoverflow.com/questions/49345903/copying-of-type-struct-memory-memory-to-storage-not-yet-supported
    function createEmployee(
        string memory info
        ) public {

        require(!employees[msg.sender].created, "Employee already exists");

        Employee storage newEmployee = employees[msg.sender];

        newEmployee.info = info;
        newEmployee.et[newEmployee.etSize++] = Exercise(block.timestamp,0,"initial",""); 
        newEmployee.created = true;
        addresses[addressMapSize++] = msg.sender;
 
    }

    // get the user and add the new excercist to them
    function addExercise(
        uint  _startTime, 
        uint  _endTime, 
        string memory _exerciseType, 
        string memory _extra

    ) public{

        require(employees[msg.sender].created, "This user doesnt exist yet");

        employees[msg.sender].et[ employees[msg.sender].etSize++] = Exercise( _startTime, _endTime, _exerciseType,_extra );


    }

    function getUserExcersises() external view returns( uint[] memory, uint [] memory, string[] memory, string[] memory ){
      

      address currAddr = msg.sender;

      require(employees[msg.sender].created, "This user has not been initialized");

      Employee storage currEmp = employees[currAddr];

      uint[] memory startTimes = new uint[](currEmp.etSize);
      uint[] memory endTimes = new uint[](currEmp.etSize);
      string[] memory exerciseTypes = new string[](currEmp.etSize);
      string[] memory extras = new string[](currEmp.etSize);

      for (uint i = 0; i < currEmp.etSize; i++) {
        startTimes[i] = currEmp.et[i].startTime;
        endTimes[i] = currEmp.et[i].endTime;
        exerciseTypes[i] = currEmp.et[i].exerciseType;
        extras[i] = currEmp.et[i].extra;
      }

      // TODO test tupple
      return (startTimes, endTimes, exerciseTypes, extras);

    }

    function getAllExcersises() external view returns( address[] memory,string[]  memory,uint[]    memory, uint[]    memory,string[]  memory,string[] memory){
      require(admin == msg.sender, "Only Admin accounts can request all excersisses");


      EmpExceStruct[] memory empexc;
      uint totalSize = 0;
      uint currentArrayValue = 0;


      for (uint i = 0; i < addressMapSize; i++) 
        totalSize += employees[addresses[i]].etSize;

      empexc =  new EmpExceStruct[](totalSize) ;

      address[] memory addrs          = new address[](totalSize);
      string[]  memory infos          = new string[](totalSize);
      uint[]    memory endTimes      = new uint[](totalSize);
      uint[]    memory startTimes     = new uint[](totalSize);
      string[]  memory exerciseTypes  = new string[](totalSize);
      string[]  memory extras         = new string[](totalSize);
      
      
      for (uint i = 0; i < addressMapSize; i++) {

        for (uint j = 0; j < employees[addresses[i]].etSize; j++) {


          // empexc[currentArrayValue].addr = addresses[i];
          // empexc[currentArrayValue].name = employees[addresses[i]].name;
          // empexc[currentArrayValue].age = employees[addresses[i]].age;
          // empexc[currentArrayValue].sex  = employees[addresses[i]].sex;
          // empexc[currentArrayValue].startTime = employees[addresses[i]].et[j].startTime;
          // empexc[currentArrayValue].endTime = employees[addresses[i]].et[j].endTime;
          // empexc[currentArrayValue].exerciseType = employees[addresses[i]].et[j].exerciseType;
          // empexc[currentArrayValue].extra = employees[addresses[i]].et[j].extra;

          
          addrs[currentArrayValue] = addresses[i]; 
          infos[currentArrayValue] = employees[addresses[i]].info;
          startTimes[currentArrayValue] = employees[addresses[i]].et[j].startTime; 
          endTimes[currentArrayValue] = employees[addresses[i]].et[j].endTime; 
          exerciseTypes[currentArrayValue] = employees[addresses[i]].et[j].exerciseType; 
          extras[currentArrayValue] = employees[addresses[i]].et[j].extra; 
          currentArrayValue++;
          
        }    

      }

      // TODO return a tupple
      return (addrs,infos,endTimes,startTimes,exerciseTypes,extras);



    }

    function getAdresses() external view returns( address[] memory){

        address[] memory addressArray =  new address[](addressMapSize);

        for (uint i = 0; i < addressMapSize; i++) {
          addressArray[i] = addresses[i];
          
        }


        return addressArray;

    }


    // TODO test!!


    // admin account
    // 0xc9c89d86c25461220e9ebc7b0c609341e5d42af7
 
 
}
 
 