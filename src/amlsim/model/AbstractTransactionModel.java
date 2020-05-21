package amlsim.model;

import amlsim.Account;
import amlsim.AMLSim;

import java.util.Random;

/**
 * Base class of transaction models
 */
public abstract class AbstractTransactionModel {

    // Transaction model ID
    public static final int SINGLE = 0;  // Make a single transaction to each neighbor account
    public static final int FAN_OUT = 1;  // Make transactions to all neighbor accounts
    public static final int FAN_IN = 2;
    public static final int MUTUAL = 3;
    public static final int FORWARD = 4;
    public static final int PERIODICAL = 5;
    public static final int GATHER_SCATTER = 6;

    protected static Random rand = new Random(AMLSim.getSeed());
//    private float transactionAmountRatio = 0.5F;  // The ratio of maximum total amount for transactions to current balance

    protected Account account;  // Account object
    protected int interval = 1; // Default transaction interval
    protected float balance;  // Current balance
    protected long startStep = -1;  // The first step of transactions
    protected long endStep = -1;  // The end step of transactions
    protected boolean isSAR = false;

    /**
     * Get the assumed number of transactions in this simulation
     * @return Number of total transactions
     */
    public int getNumberOfTransactions(){
        return (int)AMLSim.getNumOfSteps() / interval;
    }

    /**
     * Generate the assumed amount of a normal transaction
     * @return Normal transaction amount
     */
    public float getTransactionAmount(){
        // Each transaction amount should be independent from the current balance
//        float ratio = ModelParameters.generateAmountRatio();
//        return AMLSim.getSimProp().getNormalBaseTxAmount() * ratio;
        
        return AMLSim.getSimProp().getNormalBaseTxAmount();
    }

//    /**
//     * Generate the assumed amount of a suspicious transaction for SAR accounts
//     * @return Suspicious transaction amount
//     */
//    public float getSuspiciousTransactionAmount(){
//        float ratio = generateAmountRatio();
//        return AMLSim.getSimProp().getSuspiciousTxAmount() * ratio;
//    }

    /**
     * Set an account object which has this model
     * @param account Account object
     */
    public void setAccount(Account account){
        this.account = account;
        this.isSAR = account.isSAR();
    }

    /**
     * Get the simulation step range as the period when this model is valid
     * @return The total number of simulation steps
     */
    public int getStepRange(){
        // If "startStep" and/or "endStep" is undefined (-1), it returns the largest range
        long st = startStep >= 0 ? startStep : 0;
        long ed = endStep > 0 ? endStep : AMLSim.getNumOfSteps();
        return (int)(ed - st + 1);
    }

    /**
     * Return transaction type
     * @return Transaction type name
     */
    public abstract String getType();

    /**
     * Create a transaction
     * @param step Current simulation step
     */
    public abstract void sendTransaction(long step);

    /**
     * Generate the start transaction step (to decentralize transaction distribution)
     * @param range Simulation step range
     * @return random int value [0, range-1]
     */
    protected static int generateStartStep(int range){
        return rand.nextInt(range);
    }

    /**
     * Set initial parameters
     * This method will be called when the account is initialized
     * @param interval Transaction interval
     * @param balance Initial balance of the account
     * @param start Start simulation step (any transactions cannot be sent before this step)
     * @param end End step (any transactions cannot be sent after this step)
     */
    public void setParameters(int interval, float balance, long start, long end){
        this.interval = interval;
        setParameters(balance, start, end);
    }

    public void setParameters(float balance, long start, long end){
        this.balance = balance;
        this.startStep = start;
        this.endStep = end;
    }

    /**
     * Generate and register a transaction (for alert transactions)
     * @param step Current simulation step
     * @param amount Transaction amount
     * @param orig Origin account
     * @param dest Destination account
     * @param isSAR Whether this transaction is SAR
     * @param alertID Alert ID
     */
    protected void sendTransaction(long step, float amount, Account orig, Account dest, boolean isSAR, long alertID){
        if(amount <= 0){  // Invalid transaction amount
            AMLSim.getLogger().warning("Warning: invalid transaction amount: " + amount);
            return;
        }
        String ttype = orig.getTxType(dest);
        if(isSAR) {
            AMLSim.getLogger().fine("Handle transaction: " + orig.getID() + " -> " + dest.getID());
        }
        AMLSim.handleTransaction(step, ttype, amount, orig, dest, isSAR, alertID);
    }

    /**
     * Generate and register a transaction (for cash transactions)
     * @param step Current simulation step
     * @param amount Transaction amount
     * @param orig Origin account
     * @param dest Destination account
     * @param ttype Transaction type
     */
    protected void sendTransaction(long step, float amount, Account orig, Account dest, String ttype){
        AMLSim.handleTransaction(step, ttype, amount, orig, dest, false, -1);
    }

    /**
     * Generate and register a transaction (for normal transactions)
     * @param step Current simulation step
     * @param amount Transaction amount
     * @param orig Origin account
     * @param dest Destination account
     */
    protected void sendTransaction(long step, float amount, Account orig, Account dest){
        sendTransaction(step, amount, orig, dest, false, -1);
    }

    /**
     * Generate and register a transaction (for normal transactions)
     * @param step Current simulation step
     * @param amount Transaction amount
     * @param dest Destination account
     */
    protected void sendTransaction(long step, float amount, Account dest){
        this.sendTransaction(step, amount, this.account, dest);
    }

    public float getBalance(){
        return balance;
    }

}
