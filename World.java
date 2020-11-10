
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Random;
/**
 * world class
 * 
 * This is the World class it controls the inner workings of the simulation , it acts
 * as the control area where the simulation calciulates turns , moves, actions and allocation
 * of the simulation based of the reading of input information.
 * 
 * 
 * 
 * Current Version 7 
 * 
 * ~ contains stage 1 tasks like reading car to ride allocation 
 * ~ contains extra logic to determine what rides should go where results in much higher score 
 * 
 * Next version (8)
 * 
 * ~ version 8 will get a million + score for the large scenraio :P
 * ~ version 8 will also easily take the input / allocation file via String name when more scenarios are set
 * ~ check for more cars than rides incase index out of bounds 
 * Daniel Knight 
 */
public class World {
    //int x size of the world
    private int XSize;
    //int y size
    private int YSize;
    //The max number of turns for simulation determined by input file
    private int maxTurns;
    //The current turn number for the simulation 
    public static int turn;   
    //The bonus score awarded if the ride starts on time and finishes before the latest finish
    private int bonus;
    //The max number of cars in the fleet for the given simulation
    private int maxCars;
    //Number of rides to fulfil if possible (some may not be completed)
    private int rides;
    //A list of car objects for the scenario essentially a fleet
    private ArrayList<Car> fleet;   
    //A list of ride objects for the scenario
    public static ArrayList<Ride> rideList;   
    //The simple scoring mechanism keeps track of simulation score
    private Scoring score;
    //String list of what rides do what jobs (stage 1 allocation part)
    private ArrayList<String> allocation ;  

    /**
     * World constructor 
     * 
     * Sets up the scenario reads the files etc 
     * 
     * @throws fileNotFound - if the file cannot be found print stack trace
     */
    public World(String worldFile) throws FileNotFoundException {
        //Allocation intialisation 
        allocation = new ArrayList<String>();
        //Get all the info from the txt input file
        readFile(worldFile);
        //Read allocation file what cars go with what if supplied
        //readAllocationFile();
        //Print values for the rows, columns, cars, rides, bonus and steps (testing)
        //System.out.println(XSize);
        //System.out.println(YSize);
        //System.out.println(maxCars);
        //System.out.println(rides);
        //System.out.println(bonus);
        //System.out.println(maxTurns);
        //Create the world size (playing field)
        createWorldSize(XSize,YSize);
        //Scoring mechanism tracks the score
        score = new Scoring(bonus);
        //by default rusn the allocation version the one that gets the score for large scenario
        //runSimulationAllocated();
        runSimulation();
    }

    public   ArrayList<Car> getCars(){
        return fleet;
    }

    /**
     *  Return the ride list for the curr simulation
     *  
     *  @return ArrayList<Ride> of rides for the simulation
     */
    public static  ArrayList<Ride> getRides(){
        return rideList;
    }

    /**
     *  Return the curr turn for the simulation 
     *  
     *  @return the int turn number
     */
    public  static int getTurn(){
        return turn;
    }

    /**
     *  Create the world  for the self driving cars and creates where 
     *  the cars actually get accessed.
     *  
     *  @param XSize the x coords
     *  @param YSize the y coords
     */
    public void createWorldSize(int XSize, int YSize) {
        //Create the fleet array of cars
        fleet = new ArrayList(maxCars);
        //Create fleet for stagfe 1 allcoation 
        //createFleetStage1();  
        //createFleet();
        createSmarterFleet();

    }

    public int getSize(){
        return rideList.size();
    }

    /**
     *  This method will run for the stage 1 simulation where cars are told what 
     *  rides they must take and the order they do them.
     */
    public void runSimulationAllocated(){
        //Keep going from turn 0 N-1 perform the turns
        while(turn < maxTurns){
            turnAllocated();
        }
        //Total scoree for this simulation
        System.out.println("Total scores is -----> " + score.getTotalScore());
    }

    /**
     * This runs the simulation where the code decides the allocation 
     * 
     * ~ updating soon to do it a smarter way based of curr location and nearby / upcoming rides
     */
    public void runSimulation(){
        while(turn < maxTurns){
            turn();

        }
        //System.out.println(score.getTotalScore());
        writeVehicleAllocationFile();
    }

    /**
     * Take a turn in the simulation wirth allocation given via a input file this is the
     * main part of the scenario and does the calling of many functions in order to
     * actually do the simulation
     */
    public void turnAllocated() {
        //Each turn for every car do their neeed actions then incremnt turn and move on
        for(Car car : fleet){
            //If car is not in use then do not move or do anything , when a drop off is complete it will assign new jobs, or when the current car finishes a job
            if(true){
                //Run this as turns can include picking someone up and moving one space vice versa for dropping off
                car.pickedUp();
                //The movemnet from curr location to start location
                if(!(car.pickedUp())){
                    //Is this the correct x location ?
                    if(!(car.correctStartX())){
                        //if the curr x is lower than start x add
                        if(car.getCurrLocX() < car.getStartLocX()){
                            car.setCurrLocX(car.getCurrLocX() + 1);
                        }
                        //if the curr x is higher than start x subtract
                        else if(car.getCurrLocX() > car.getStartLocX()){
                            car.setCurrLocX(car.getCurrLocX() - 1);
                        }
                    }
                    //Is this tthe correct y location ?
                    else if(!(car.correctStartY())){

                        if(car.getCurrLocY() < car.getStartLocY()){
                            //if the curr y is lower than start y add
                            car.setCurrLocY(car.getCurrLocY() + 1);
                        }
                        //if the curr y is lower than start y subtract
                        else if(car.getCurrLocY() > car.getStartLocY()){
                            car.setCurrLocY(car.getCurrLocY() - 1);
                        }
                    }
                }
                //If the car has picked its passenger up then start moving one space either x or y 
                else if(car.pickedUp()){
                    //The code will not reach here until the passenger is collected and car is on start x and y 
                    //Is this the correct x location ?
                    if(!(car.correctX())){
                        //Take note of the spaces moved for the simulation scoring
                        car.increaseSpaces();
                        //If the curr x is lower than start x add?
                        if(car.getCurrLocX() < car.getEndLocX()){
                            car.setCurrLocX(car.getCurrLocX() + 1);
                        }
                        //If the curr x is higher than start x subtract
                        else if(car.getCurrLocX() > car.getEndLocX()){
                            car.setCurrLocX(car.getCurrLocX() - 1);
                        }
                    }
                    //correct y location ?

                    else if(!(car.correctY())){
                        //Take note of the spaces moved for the simulation scoring
                        car.increaseSpaces();
                        if(car.getCurrLocY() < car.getEndLocY()){
                            //if the curr y is lower than start y add
                            car.setCurrLocY(car.getCurrLocY() + 1);
                        }
                        //if the curr y is lower than start y subtract
                        else if(car.getCurrLocY() > car.getEndLocY()){
                            car.setCurrLocY(car.getCurrLocY() - 1);
                        }
                    }
                }
                //Drop the passenger off and assign a new ride to this car as the passnenger has reacehd their correct destination
                if(car.correctX() && car.correctY() && car.getPassenger()){
                    //Set the ride as complete  
                    rideList.get(car.getID()).setComplete();
                    //Add the scores if ride arrives before latest finish + any bonus if applicable
                    if(turn < rideList.get(car.getID()).getLatestFinish()){
                        score.addScore(car.getSpaces());
                        //If ride started on time add bonus
                        if(car.getBonus()){
                            score.addBonusScore();
                            var++;
                        }
                    }
                    //Drop off passenger
                    car.dropoff();
                    //Set the next ride from this cars list if remaining
                    car.setRide();
                    //Reset spaces moved from start to end ride intersections
                    car.resetSpaces();
                    //Reset the bonus field
                    car.resetBonus();
                }
            }
        }
        //increment turn counter / println
        System.out.println("TURN --> " + turn);
        turn++;
    }
    int var = 0;

    /**
     * Take a turn in the simulation up to max amount of turns then end , very similar to the 
     * previous code howver this one will assign based off the different factors associated with the 
     * simulation (trying to get the best and most optimal score)
     */
    public void turn() {
        for(Car car : fleet){
            //if car is not in use then do not move or do anything , when a drop off is complete it will assign new jobs, or when the current car finishes a job
            if(car.inUse()){
                //run this as turns can include picking someone up and moving one space vice versa for dropping off
                car.pickedUp();
                //the movemnet from curr location to start location
                if(!(car.pickedUp())){
                    //is this the correct x location ?
                    if(!(car.correctStartX())){
                        if(car.getCurrLocX() < car.getStartLocX()){
                            car.setCurrLocX(car.getCurrLocX() + 1);
                        }
                        else if(car.getCurrLocX() > car.getStartLocX()){
                            car.setCurrLocX(car.getCurrLocX() - 1);
                        }
                    }
                    //correct y location ?

                    else if(!(car.correctStartY())){
                        if(car.getCurrLocY() < car.getStartLocY()){

                            car.setCurrLocY(car.getCurrLocY() + 1);
                        }
                        else if(car.getCurrLocY() > car.getStartLocY()){
                            car.setCurrLocY(car.getCurrLocY() - 1);
                        }
                    }
                }
                //if the car has picked its passenger up then start moving one spaceeither x or y 
                else if(car.pickedUp()){
                    //is this the correct x location ?

                    if(!(car.correctX())){
                        car.increaseSpaces();
                        if(car.getCurrLocX() < car.getEndLocX()){
                            car.setCurrLocX(car.getCurrLocX() + 1);
                        }
                        else if(car.getCurrLocX() > car.getEndLocX()){
                            car.setCurrLocX(car.getCurrLocX() - 1);
                        }
                    }
                    //correct y location ?

                    else if(!(car.correctY())){
                        car.increaseSpaces();
                        if(car.getCurrLocY() < car.getEndLocY()){

                            car.setCurrLocY(car.getCurrLocY() + 1);
                        }
                        else if(car.getCurrLocY() > car.getEndLocY()){
                            car.setCurrLocY(car.getCurrLocY() - 1);
                        }
                    }
                }
                //drop the passenger off and assign a new ride to this car
                if(car.correctX() && car.correctY() && car.getPassenger()){
                    //set the ride as complete used for end of simulation
                    rideList.get(car.getID()).setComplete();
                    //add the scores if ride arrives before latest finish
                    if(turn < rideList.get(car.getID()).getLatestFinish()){
                        score.addScore(car.getSpaces());
                        //if it started on time add bonus
                        if(car.getBonus()){
                            score.addBonusScore();
                        }
                    }
                    //Drop off passenger
                    car.dropoff();
                    //Reset spaces moved from start to end ride intersections
                    car.resetSpaces();
                    car.resetBonus();
                    //Assign new jobs to unused cars 
                    if(jobsLeft() > 0){

                        assignJobsSmarterVer1(car);
                    }
                }

            }
        }
        //increment turn counter / turn number
        //System.out.println("TURN --> " + turn);
        turn++;
    }

    public int jobsComplete(){
        int i = 0;
        for(Ride r : rideList){
            if(r.getTaken()){
                i++;
            }
        }
        return i;
    }

    public int jobsLeft(){
        int i = 0;
        for(Ride r : rideList){
            if(!(r.getTaken())){
                i++;
            }
        }
        return i;
    }

    public ArrayList<Ride> whatsLeft(){
        ArrayList<Ride> a = new ArrayList();
        for(Ride r : rideList){
            if(!(r.getTaken())){
                a.add(r);
            }
        }
        return a;
    }

    public ArrayList<Ride> takenButNotComplete(){
        ArrayList<Ride> a = new ArrayList();
        for(Ride r : rideList){
            if(r.getTaken() && !(r.getComplete())){
                a.add(r);
            }
        }
        return a;
    }

    /**
     *  Create the fleet of self driving cars  all start at coords x = 0  and y = 0
     */
    public void createSmarterFleet() {
        //Create a car for the fleet up to the maximum amount specified in the file 
        for(int x = 0 ; x < maxCars; x++) {
            //Initialise the car into the array
            //code here for assigning if rides are rapid
            int currRideIndex;
            if(checkEarlyStarts()){
                currRideIndex= lowStarts1() ;
            }
            //large x and y size
            else if(checkGridSize()){
                currRideIndex= Metro1() ;
            }
            else {
                //smaller grids lots of rides , small car count
                currRideIndex= initialSmallgridLowCars() ;
            }
            Ride StartRide = rideList.get(currRideIndex);
            //Ride is taken
            StartRide.setTaken();
            //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use, the latest finish (updating new ride info)
            fleet.add(new Car(x+1,currRideIndex, StartRide.getStartX(),StartRide.getStartY(),StartRide.getEndX(),StartRide.getEndY(),0,0, StartRide.earlyStart(), true, StartRide.getLatestFinish()));
        }
    }

    public boolean checkEarlyStarts(){
        boolean b = true;
        for(Ride r : rideList){
            if(r.earlyStart() > 20){
                b = false;
            }
        }
        return b;
    }
    
    public boolean checkGridSize(){
        boolean b = true;
        if(XSize < 10000 || YSize < 10000 ){
            b = false;
        }
        return b;
    }

    /**
     * My attempt at assigning based of if a car is not in use then assign another job 
     * tries to ensure that every ride is fulfiled.
     * 
     * ~ Version 3
     * ~ doesn't consider if a car is near rides yet but will in order to get a better score
     */
    public void assignJobsSmarterVer1(Car car){
        //Check all of our cars

        if(!(car.inUse())){
            int currRideIndex;
            //quick starts low rides
             if(checkEarlyStarts()){
                currRideIndex= lowStarts2(car) ;
            }
            //gride size larger
            else if(checkGridSize()){
                currRideIndex= COPY6POINT8mill(car) ;
            }
            else {
                //smaller grids lots of rides , small car count
                currRideIndex= smallGridLowCarsLotsRides(car) ;
            }
           
            Ride ride = rideList.get(currRideIndex);
            //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use, the latest finish (updating new ride info)
            car.updateCarRide(ride.getID(), ride.getStartX(), ride.getStartY(), ride.getEndX(),ride.getEndY(), car.getCurrLocX(), car.getCurrLocY(),car.getEarliestTurn(), true,ride.getLatestFinish());
            //this ride is in progress so don't assign to another car (marked as taken)    
            ride.setTaken();
            //car now in use on a new ride
            car.setInUse();

        }

    }

    public int initialSmallgridLowCars(){    
        //assume max movement x ands y 
        int overallWait = XSize + YSize;
        //the index
        int index= 0;
        //counter
        int counter = 0;

        int maxScorePerRide = 0;
        int ES = maxTurns;

        int currTurn = getTurn();
        boolean val = true;
        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            //movement from 0,0 to the start x and y (find the shortest distance)
            int tempTotal = r.getStartX() + r.getStartY();
            //the earliest start
            int currES = r.earlyStart();
            //calculation = ES - (SX + SY)
            int calculation = currES - tempTotal;

            int sX = r.getStartX();
            int sY = r.getStartY();
            //ride end location
            int eX = r.getEndX();
            int eY = r.getEndY();

            //totalEnd calcs
            int totalEndx = eX - sX;
            if(totalEndx < 0){
                totalEndx = totalEndx * -1;
            }
            int totalEndy = eY - sY;
            if(totalEndy < 0){
                totalEndy = totalEndy * -1;
            }

            int totalToEnd = totalEndx + totalEndy;

            //total for jouney all spaces moved  so from cx and cy to start location and from strat to endx and endy 
            //check if the ride from cx & cy + sx +sy + sx + sy -> ex and ey < latestfinish
            int startToFinish = tempTotal + totalToEnd ;
            int wouldBeTurn = getTurn() + startToFinish;

            if(calculation < 0){
                calculation = calculation * -1;
            }
            //calculation <= overallWait
            if(!(r.getTaken()) &&  calculation < overallWait   ){
                index = counter;
                ES = currES;
                overallWait = calculation;
                val = false;
            }            
            
            counter++;
        }

        if(val){
            index = randIndex();
        }

        return index;
    }

    public int smallGridLowCarsLotsRides(Car car){
        int index = 0;
        int counter = 0;
        //11,018,367

        int currTurn = getTurn();

        //next earliest start 
        int ES  = maxTurns;
        boolean val = true;

        int totalMovement = XSize + YSize;
        for(Ride r : rideList){ 
            //stuff for allocation
            int sX = r.getStartX();
            int sY = r.getStartY();

            int cX = car.getCurrLocX();
            int cY = car.getCurrLocY();
            // sx - cx =the movement from cars x to start x
            int totalX = sX - cX;

            if(totalX < 0){
                totalX = totalX * -1;
            }
            // sy - cy = the movement on the Y axis
            int totalY = sY - cY;
            if(totalY < 0){
                totalY = totalY * -1;
            }

            //the total spaces needed to move fro curr x and curr y to start x and start y
            int total = totalX + totalY;

            //ride end location
            int eX = r.getEndX();
            int eY = r.getEndY();

            //totalEnd calcs
            int totalEndx = eX - sX;
            if(totalEndx < 0){
                totalEndx = totalEndx * -1;
            }
            int totalEndy = eY - sY;
            if(totalEndy < 0){
                totalEndy = totalEndy * -1;
            }

            int totalToEnd = totalEndx + totalEndy;

            //total for jouney all spaces moved  so from cx and cy to start location and from strat to endx and endy 
            //check if the ride from cx & cy + sx +sy + sx + sy -> ex and ey < latestfinish
            int startToFinish = total + totalToEnd ;

            int wouldBeTurn = getTurn() + startToFinish;

            int es = r.earlyStart();

            if( !(r.getTaken())  && (es < ES && wouldBeTurn < r.getLatestFinish() )){
                index = counter;
                totalMovement = total;
                ES = es;
                val = false;
            }

            counter++;
        }

        if(val){
            index = randIndex();
        }

        return index;
    }

    /**
     * Usefull for when the starts are low and the rides are rushed (initial setUp);
     */
    public int lowStarts1(){
        //assume max movement x ands y 
        int spacesFromZero = XSize + YSize;
        //the index
        int index= 0;
        //counter
        int counter = 0;

        int maxScorePerRide = 0;

        int currTurn = getTurn();
        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            int tempTotal = r.getStartX() + r.getStartY();
            /**
            int startX = r.getStartX();
            int startY = r.getStartY();

            int endX = r.getEndX();
            int endY = r.getEndY();

            int tempScoreX = endX - startX;
            int tempScoreY = endY - startY;

            //non negative val
            if(tempScoreX < 0){
            tempScoreX = tempScoreX * -1;
            }
            //non negative val
            if(tempScoreY < 0){
            tempScoreY = tempScoreY * -1;
            }

            int totalScore =tempScoreX + tempScoreY;*/

            if(tempTotal < spacesFromZero && !(r.getTaken()) && currTurn + tempTotal < maxTurns && currTurn + tempTotal < r.getLatestFinish()   ){
                spacesFromZero = tempTotal;
                //maxScorePerRide = totalScore;
                index = counter;

            }
            counter++;
        }

        return index;
    }

    /**
     * Low starts but assigning after first car is chosen
     */
    public int lowStarts2(Car car){
        int index = 0;
        int counter = 0;

        int currTurn = getTurn();
        int maxScorePerRide = 0;

        int spacesFromCarLocation = XSize + YSize;

        for(Ride r : rideList){

            //stuff for allocation
            int rideX = r.getStartX();
            int rideY = r.getStartY();

            int carX = car.getCurrLocX();
            int carY = car.getCurrLocY();

            int xSpacesMoved = carX - rideX;
            int ySpacesMoved = carY - rideY;
            //non negative val
            if(xSpacesMoved < 0){
                xSpacesMoved = xSpacesMoved * -1;
            }
            //non negative val
            if(ySpacesMoved < 0){
                ySpacesMoved = ySpacesMoved * -1;
            }
            int tempTotal = xSpacesMoved + ySpacesMoved;
            //comparison of the total spaces needed to move
            if(tempTotal < spacesFromCarLocation && !(r.getTaken()) && currTurn + tempTotal < maxTurns && currTurn + tempTotal < r.getLatestFinish()  ){
                spacesFromCarLocation = tempTotal;

                index = counter;
            }
            counter++;
        }

        return index;
    }
    
    /**
     * Better suited for tests where cars are limited but rides are evenly spaced and a lot of rides exist
     */
    public int biggerGridsSpacedTurnsLowCars(Car car){
        int index = 0;
        int counter = 0;

        int currTurn = getTurn();
        

        int totalMovement = XSize + YSize;
        int ES = maxTurns;

        for(Ride r : rideList){ 
            //stuff for allocation
            int sX = r.getStartX();
            int sY = r.getStartY();

            int cX = car.getCurrLocX();
            int cY = car.getCurrLocY();
            // sx - cx =the movement from cars x to start x
            int totalX = sX - cX;

            if(totalX < 0){
                totalX = totalX * -1;
            }
            // sy - cy = the movement on the Y axis
            int totalY = sY - cY;
            if(totalY < 0){
                totalY = totalY * -1;
            }
            
            //the total spaces needed to move 
            int total = totalX + totalY;
             //take into consideration the value of ES 
            int currES = r.earlyStart();
            
            //es - the total 
            int waiting = currTurn + total; 
            
            int eX = r.getEndX();
            int eY = r.getEndY();
            
           
            
            if(total < totalMovement && !(r.getTaken()) && waiting < ES ){
                index = counter;
                ES = currES;
                totalMovement = total;
            }

            
            
            counter++;
        }

        return index;
    }
    
        public int Metro1(){    
        
        //the index
        int index= 0;
        //counter
        int counter = 0;

     
        int ES = maxTurns;
        int ESCalc = maxTurns;

        int currTurn = getTurn();
        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            
            //the earliest start
            int currES = r.earlyStart();
            
            int sx = r.getStartX();
            int sy = r.getStartY();
            
            int total= sx + sy;
            
            
            
            
            int totalCalc = currES - total;
           

            //calculation <= overallWait
            if(!(r.getTaken()) &&  totalCalc < ES ){
                index = counter;
                ES = totalCalc;
                
            }            
            //alone score is 886,715 , put an OR 886620, 869,735 is with claculation <= overall wait
            counter++;
        }

        return index;
    }
    
        public int COPY6POINT8mill(Car car){
                //the index
        int index= 0;
        //counter
        int counter = 0;

     
        int ES = maxTurns;
        int lowSpacesMoved = maxTurns;

        int currTurn = getTurn();
        //all cars start at 0 0  therefore the x + y is the spaces needed to move 
        for(Ride r : rideList)   {
            if(r.getStartX() != 9999){
            int cx = car.getCurrLocX();
            int cy = car.getCurrLocY();
            
            int sx = r.getStartX();
            int sy = r.getStartY();
            
            int totalx = sx - cx;
            int totaly = sy - cy;
            
            if(totalx < 0){
                totalx = totalx * -1;
            }
            if(totaly < 0){
                totaly = totaly * -1;
            }
            
            int totalCalc = totalx + totaly;
            //the earliest start
            int currES = r.earlyStart();
           

            //calculation <= overallWait
            if(!(r.getTaken()) &&  currES < ES && totalCalc < lowSpacesMoved ){
                index = counter;
                lowSpacesMoved = totalCalc;
                ES = currES;
                
            } 
        }
            //alone score is 886,715 , put an OR 886620, 869,735 is with claculation <= overall wait
            counter++;
        }

        return index;
    }
    

    public int getScore(){
        return score.getTotalScore();
    }

    public int randIndex(){
        ArrayList<Integer> indexs = new ArrayList();
        int counter = 0;
        for(Ride r : rideList){
            if(!(r.getTaken()) && !(r.getComplete()) ){
                indexs.add(counter);
            }
            counter ++;
        }

        Random rand = new Random();
        int in = rand.nextInt(indexs.size());

        return indexs.get(in);
        //pick a random one 
    }

    /**
     * Basic reading of a text file to get the input
     * for the given scenario / simulation. Set up a list of rides 
     * ready for the vehicles.
     * 
     * TODO : 
     * ~ the ability to pick a file via GUI makes it more
     * user friendly 
     * 
     * ~ tidy up and add exceptions for incorrect files etc /
     * 
     * @throws FileNotFoundException
     */

    public void readFile(String worldFile) throws FileNotFoundException {
        //Get the file
        rideList = new ArrayList();
        //"D:\\CO655 project\\input.txt"
        FileReader file = new FileReader(worldFile);
        //Try to read the file using a buffered reader
        try(BufferedReader reader = new BufferedReader(file)){
            //The first line which contains the values in order rows , columns, vehicles, rides, bonus , steps 
            String[] firstLine =  reader.readLine().split(" ");
            // Max Rows
            XSize = Integer.parseInt(firstLine[0]); 
            //Max Columns
            YSize = Integer.parseInt(firstLine[1]); 
            //Car count
            maxCars = Integer.parseInt(firstLine[2]);   
            //Number of rides
            rides = Integer.parseInt(firstLine[3]);
            //Bonus value
            bonus = Integer.parseInt(firstLine[4]); 
            //Maximum turn in the simulation
            maxTurns = Integer.parseInt(firstLine[5]);  
            //The readline values etc
            String values ;
            int rideNum = 0 ;
            //Do until file is finished so empty lines null
            do  {
                //Get the values from curr line            
                values = reader.readLine();
                //If null don't print
                if(values != "" && values != null) {
                    //Split at regex " " (spaces)
                    String[] currLine =  values.split(" ");
                    //X coord for ride
                    int startX = Integer.parseInt(currLine[0]); 
                    //Y coord for ride
                    int startY = Integer.parseInt(currLine[1]); 
                    //X end coord for ride
                    int endX = Integer.parseInt(currLine[2]);   
                    //Y end coord for ride
                    int endY = Integer.parseInt(currLine[3]);
                    //Earliest pickup for ride
                    int earliestStart = Integer.parseInt(currLine[4]); 
                    //Latest finish for ride
                    int latestFinish = Integer.parseInt(currLine[5]);  
                    //Create the rides and store in the simultions list of rides.
                    rideList.add(new Ride(rideNum,startX, startY, endX, endY, earliestStart, latestFinish, false));
                    //Testing ...
                    //System.out.println(startX);
                    //System.out.println(startY);
                    //System.out.println(endX);
                    //System.out.println(endY);
                    //System.out.println(earliestStart);
                    //System.out.println(latestFinish);
                    //System.out.println("");
                }
                //Inc the ride number how many rides there are in curr simulation
                rideNum++;
                //Condition to stop the reading when value is null
            }  while(values != null); { 
            }
        }
        //Catch any exceptions basic atm will update soon
        catch(Exception e){
            e.printStackTrace();
            //System.out.println();
        }
    }

    /**
     *  Create the fleet of self driving cars  all strat at coords x = 0  and y = 0 for 
     *  the stage one allcation task
     */
    public void createFleetStage1() {
        //Create a car for the fleet up to the maximum amount specified in the file 
        for(int x = 0 ; x < maxCars; x++) {          
            //Give the ride the allocation of rides assigned to car 
            fleet.add(new Car(x+1, allocation.get(x)));
        }
    }

    /**
     * Basic reading of a text file to get the input
     * for the given scenario / simulation. Set up a list of rides 
     * ready for the vehicles.
     * 
     * 
     * @throws FileNotFoundException
     */

    public void readAllocationFile() throws FileNotFoundException {
        //Get the allocation file
        //D:\\CO655 project\\cars.txt
        //FileReader file = new FileReader("E:\\CO655 project\\d_metropolis.out");
        //E:\\CO655 project\\VersionsStag2\\Version2AttemptCurrent\\BlueJSimuCopyNAP\\submissionFile.txt
        FileReader file = new FileReader("E:\\CO655 project\\VersionsStag2\\Version2AttemptCurrent\\BlueJSimuCopyNAP\\submissionFile.txt");
        //Try to read the file using a buffered reader
        try(BufferedReader reader = new BufferedReader(file)){
            //Storing local values etc
            String values ;
            int rideNum = 0;
            //Do until file is finished so empty lines 
            do  {
                //Get the values                
                values = reader.readLine();
                //If null don't print
                if(values != null) {
                    //what cars will do what rides
                    allocation.add(values);
                }
                rideNum++;
                //condition to stop the reading when null is found (end of file)
            }  while(values != null); { 
            }
        }
        // ~ catch any exceptions basic atm will update soon
        catch(Exception e){
            e.printStackTrace();
            //System.out.println();
        }
    }

    /**
     * This method will creat an allocation file for the simulation based of the code assigning 
     * cars to rides based off a number of factors to get the highest ands optimal scores
     */
    public void writeVehicleAllocationFile(){

        //Create the submission file
        //File file = new File("submissionFile" + ".txt");
        //Write to file not read
        //file.setWritable(true);
        //Writer used to actually write a txt file
        //FileWriter writer = new FileWriter(file);
        //For all the cars write their allocation of rides completed
        for(Car car: fleet){
            //writer.write(car.getRidesTotal() + " " + car.getRidesCompleted() + "\r\n");
            System.out.println(car.getRidesTotal() + " " + car.getRidesCompleted() );
        }

        //Close the file we are done writing
        //writer.close();
        //Readonly for file no tampering
        //file.setReadOnly();

    }
    
    
    /**
     * This method will creat an allocation file for the simulation based of the code assigning 
     * cars to rides based off a number of factors to get the highest ands optimal scores
     */
    public void writeVehicleOutputFile(){
        try{
            //Create the submission file
            File file = new File("submissionFile" + ".txt");
            //Write to file not read
            file.setWritable(true);
            //Writer used to actually write a txt file
            FileWriter writer = new FileWriter(file);
            //For all the cars write their allocation of rides completed
            for(Car car: fleet){
                writer.write(car.getRidesTotal() + " " + car.getRidesCompleted() + "\r\n");
            }
            //Close the file we are done writing
            writer.close();
            //Readonly for file no tampering
            file.setReadOnly();
        }
         // ~ catch any exceptions basic atm will update soon
        catch(IOException e){
            //what went wrong?
            e.printStackTrace();
        }
    }
}
