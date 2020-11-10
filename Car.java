import java.util.ArrayList;
/**
 * The Car class is the workhorse of the simulation does the computing and completes
 * rides with the assitance of the World class and supllies info to Scoring class 
 * using the Ride class for info.
 * 
 * Version 7
 * 
 * Daniel Knight
 */
public class Car {
    //The id for the ride (current) and dynamic
    private int ID;
    //Carrying a person or not , is the ride free?
    private boolean passenger;
    //The start location where the car needs to be X
    private int  startLocX;
    //The start location where the car needs to be Y
    private int  startLocY;
    //The end location where the car needs to be X
    private int  endLocX;
    //The end location where the car needs to be Y
    private int  endLocY;
    //The current location where the car is on X axis
    private int  currLocX;
    //The current location where the car is on Y axis
    private int  currLocY;
    //The latest finish
    private int latestFinish;
    //The current location where the car is on Y axis
    private int  turnPickup;
    //String list of rides and the order they need to be in
    private String ridesCompleted;
    //Curr ride
    private Ride currRide;
    //The cars ID number
    private int carNum;
    //Spaces moved
    private int spacesMoved;
    //Will tehy recieve a bonus for the current ride
    private boolean bonus;
    //Has the car moved yet
    private boolean hasNotMoved;
    //Are they at the start location?
    private boolean startLoc;
    //Is this car in use
    private boolean inUse;
    //The ride allocation this car is assigned (stage 1 usage)
    private String rideAllocation;
    //Number of rides this car must complete
    private int numberOfRides;
    //The list of ride Indexs where this car can get the ifnormation that it requires for the project 
    private ArrayList<Integer> rideIndexs;
    //The curr index value 
    int currIndex = 0;
    
    private boolean inRide;
    
    //ride  umber completeed 
    private int ridesTotal = 0;

    /**
     * The first cosntructor for the Car where the supplied info is given
     * @carNum             the ID for this car
     * @rideNum            the ride number
     * @startLocX          the start x location
     * @startLocY          the start y location
     * @endLocX the        end x location
     * @endLocY the        end y location
     * @currLocX           the curr x location
     * @currLocY           the curr y location
     * @turnPickup         the turn ride should start
     * @latestFinish       the latest finish 
     * @inUse              true or false is being used
     */

    public Car(int carNum ,int ID , int startLocX, int startLocY, int endLocX, int endLocY, int currLocX, int currLocY, int turnPickup, boolean inUse, int latestFinish) {
        //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use
        this.carNum = carNum;
        this.passenger = passenger;
        this.startLocX = startLocX;
        this.startLocY = startLocY;
        this.endLocX = endLocX;
        this.endLocY = endLocY;
        this.currLocX = currLocX;
        this.currLocY = currLocY;
        this.ID = ID;
        this.turnPickup = turnPickup;
        this.inUse = inUse;
        this.latestFinish = latestFinish;
        //has not moved 
        hasNotMoved = true;
        //passenger yes or no
        passenger = false;
        //pickup sometimes the curr x and y is the same as the rides pickup location 
        pickedUp();
        //no rides done yet
        ridesCompleted = "";
        //no bonus yet
        bonus = false;
        inRide = true;
    }

    /**
     * Car constructor 2 used for the Stage 1 allocation task
     * @carNum             the ID for this car
     * @rideAllocation     the rides it has to do
     */
    public Car(int carNum , String rideAllocation){        
        this.carNum = carNum;
        this.rideAllocation = rideAllocation;
        getRides();
    }

    /**
     * Gewt the rides needed for this car and add to a list of the rides 
     * it is assigned to do for this simulation
     */
    public void getRides(){
        //Intialise the rides
        rideIndexs = new ArrayList();
        //The rideList
        String[] rideList =  rideAllocation.split(" ");
        //Total number of rides needed to do 
        numberOfRides = Integer.parseInt(rideList[0]);
        //Add all the rides index values
        for(int x = 1; x < rideList.length; x++ ){
            rideIndexs.add(Integer.parseInt(rideList[x]));
        }
        //No passenger yet
        passenger = false;
        ridesCompleted = "";
        bonus = false;
        //Set up the next ride (first ride in this case)
        setRide();
    }

    /**
     * Set the next ride up from the list and allow the cra to complete its actions
     * for thsi particular ride 
     */
    public  void setRide(){
        //If the curr Index is not bigger than the ride list then get the next ride
        if(currIndex < rideIndexs.size()){
            //The index
            int rideNum = rideIndexs.get(currIndex);
            //The ride we need
            Ride r = World.getRides().get(rideNum);
            //The update method to update the cars fields
            updateCarRide(rideNum, r.getStartX(), r.getStartY(), r.getEndX(), r.getEndY(), currLocX, currLocY, r.earlyStart(), true, r.getLatestFinish());
            //Car is now taken
            r.setTaken();
            //Inc the index to get next ride if we need to 
            currIndex++;
        }
    }

    /**
     * Get the car number 
     * @return the CarNum
     */
    public int getCarNum(){
        return carNum;
    }

    /**
     * Increase the spaces moved from start x and y intersection used for
     * scoring during the simulation
     * 
     */
    public void increaseSpaces(){
        spacesMoved ++;
    }

    /**
     * Get the rides completed for this car
     * @return the curr rideCompleted 
     */
    public String getRidesCompleted(){
        return ridesCompleted;
    }

    /**
     * Reset the spaces moved ready for the next new ride if rides remain
     */
    public void resetSpaces(){
        spacesMoved = 0;
    }

    /**
     * Gewt the spaces moved for this ride used for scoring
     * @return the spaces moved 
     */
    public int getSpaces(){
        return spacesMoved;
    }

    /**
     * Get the latest finish to recieve points used in scoring etc 
     * @return the latestFinish
     */
    public int getLatestFinsh(){
        return latestFinish;
    }

    /**
     * Get the ID
     * @return the ID
     */
    public int getID(){
        return ID;
    }

    /**
     * Get the passnger if they have one
     * @return the true or false if a passenger is present
     */
    public boolean getPassenger(){
        return passenger;
    }

    /**
     * Get the earliest start for the journey
     * @return the turn to pickup 
     */
    public int getEarliestTurn()
    {
        return turnPickup;
    }

    /**
     * The update for the Car where the supplied info is given for a new ride
     * @ID                 the ride number
     * @startLocX          the start x location
     * @startLocY          the start y location
     * @endLocX the        end x location
     * @endLocY the        end y location
     * @currLocX           the curr x location
     * @currLocY           the curr y location
     * @turnPickup         the turn ride should start
     * @latestFinish       the latest finish 
     */
    public void updateCarRide(int ID ,  int startLocX, int startLocY, int endLocX, int endLocY, int currLocX, int currLocY, int turnPickup, boolean inUse, int latestFinish) {
        //Set the values for the 
        //ID, passenger , start x , start y , end x, end y , curr x, curr y, turn pickup (earliest start), car in  use
        this.passenger = passenger;
        this.startLocX = startLocX;
        this.startLocY = startLocY;
        this.endLocX = endLocX;
        this.endLocY = endLocY;
        this.currLocX = currLocX;
        this.currLocY = currLocY;
        this.ID = ID;
        this.turnPickup = turnPickup;
        this.inUse = inUse;
        this.latestFinish = latestFinish;
        World.getRides().get(ID).setTaken();
        hasNotMoved = true;
        passenger = false;
        pickedUp();
        inRide = true;
    }
    
     /**
     * Get the bonus status for curr ride
     * @return the bonus if its true or false
     */
    public boolean getBonus(){
        return bonus;
    }
    
     /**
     * Set the bonus id this ride will recieve a bonus providing it finishes before latest finish
     */
    public void setBonus(){
        bonus = true;
    }
    
    /**
     * Reset the bonus ready for a new ride
     */
    public void resetBonus(){
        bonus = false;
    }
    
    /**
     * Pickup the pasenger for the ride if the curr x and y is == start and 
     * the curr turn is gerater than or equal to the earliest start of the ride
     * @return the passenger value 
     */
    public boolean pickedUp(){
        if(passenger == false){
            if(World.getTurn() >= getEarliestTurn()){
                if(getCurrLocX() == getStartLocX() && getCurrLocY() == getStartLocY()){
                    passenger = true;
                    if(World.getTurn() == getEarliestTurn()){
                        setBonus();
                    }
                }

            }
        }

        return passenger;
    }
    
    /**
     * Get the cars usage status
     * @return the cars usage status
     */
    public boolean inUse(){
        return inUse;
    }
    
    /**
     * Set the car to being in use
     * @return the in use part
     */
    public void setInUse(){
        inUse = true;
    }
    
    /**
     * Drop off the passneger by reseting passenger status and add the curr ride as completed 
     * to the completed list set in use to false.
     */
    public void dropoff(){
        passenger = false;
        ridesCompleted = ridesCompleted + (ID + " ");
        ridesTotal++;
        inUse = false;
        
    }
    
    /**
     * Get the total number of rides completed in the simulation 
     * @return ridesTotal 
     * 
     */
    public int getRidesTotal(){
        return ridesTotal;
    }
    
    /**
     * Is the curr x the correct start?
     * 
     * @return whether the curr location x matches start x
     */
    public boolean correctStartX(){
        boolean correctsx = false;
        if(getCurrLocX() == getStartLocX()){
            correctsx = true;
        }
        return correctsx;
    }
    
    /**
     * Is the curr y the correct start?
     * 
     * @return whether the curr location y matches start y
     */
    public boolean correctStartY(){
        boolean correctsy = false;
        if(getCurrLocY() == getStartLocY()){
            correctsy = true;
        }
        return correctsy;
    }

     /**
     * Is the curr x the correct end Location ?
     * 
     * @return whether the curr location x matches end x
     */
    public boolean correctX(){
        boolean correctx = false;
        if(getCurrLocX() == getEndLocX()){
            correctx = true;
        }
        return correctx;
    }

    /**
     * Is the curr y the correct end Location ?
     * 
     * @return whether the curr location y matches end y
     */
    public boolean correctY(){
        boolean correctY = false;
        if(getCurrLocY() == getEndLocY()){
            correctY = true;
        }
        return correctY;
    }
    
     /**
     * Get the moved status
     * 
     * @return whether the car has moved
     */

    public boolean notMoved(){
        return hasNotMoved;
    }
    
    /**
     * Set the moved status to true
     * 
     * @return whether the curr location x matches end x
     */
    public void setMoved(){
        hasNotMoved = false;
    }

    public void UnsetMoved(){
        hasNotMoved = true;
    }
    
    /**
     * Get the passenger part
     * 
     * @return true or false the passenger status
     */
    public boolean isPassenger() {
        return passenger;
    }
    
    /**
     * Set the passenger status 
     * 
     * @param passenger the status
     */
    public void setPassenger(boolean passenger) {
        this.passenger = passenger;
    }
    
        
    /**
     * Get the start x coord
     * 
     * @return the start x coord
     */
    public int getStartLocX() {
        return startLocX;
    }
    
     /**
     * Set the start x coord
     * 
     * @param the start x coord
     */
    public void setStartLocX(int startLocX) {
        this.startLocX = startLocX;
    }
    
    
    /**
     * Get the start y coord
     * 
     * @return the start y coord
     */
    public int getStartLocY() {
        return startLocY;
    }
    
        
    /**
     * Set the start y coord
     * 
     * @param the start y coord
     */
    public void setStartLocY(int startLocY) {
        this.startLocY = startLocY;
    }
    
    /**
     * Get the end x coord
     * 
     * @return the end x coord
     */
    public int getEndLocX() {
        return endLocX;
    }
    
    /**
     * Set the end x coord
     * 
     * @param the end x coord
     */
    public void setEndLocX(int endLocX) {
        this.endLocX = endLocX;
    }
    
     /**
     * Get the end y coord
     * 
     * @return the end y coord
     */
    public int getEndLocY() {
        return endLocY;
    }
    
     /**
     * Set the end x coord
     * 
     * @param the end x coord
     */
    public void setEndLocY(int endLocY) {
        this.endLocY = endLocY;
    }
    
    /**
     * Get the curr x coord
     * 
     * @return the curr x coord
     */
    public int getCurrLocX() {
        return currLocX;
    }

    /**
     * Set the curr x coord
     * 
     * @param the curr x coord
     */ 
    public void setCurrLocX(int currLocX) {
        this.currLocX = currLocX;
    }
    
    /**
     * Get the curr y coord
     * 
     * @return the curr y coord
     */ 
    public int getCurrLocY() {
        return currLocY;
    }
    
    /**
     * Set the curr y coord
     * 
     * @param the curr y coord
     */ 
    public void setCurrLocY(int currLocY) {
        this.currLocY = currLocY;
    }

}