//
// Note: No specific bank models are used for this fraud transaction model class.
//

package amlsim.model.aml;

import amlsim.Account;

import java.util.*;

/**
 * Cycle transaction model
 */
public class CycleTypology extends AMLTypology {

    // Transaction schedule
    private long[] steps;  // Array of simulation steps when each transaction is scheduled to be made
    public static final int FIXED_INTERVAL = 0;  // All accounts send money in order with the same interval
    public static final int RANDOM_INTERVAL = 1;  // All accounts send money in order with random intervals
    public static final int UNORDERED = 2;  // All accounts send money randomly
    private float amount = 0.0F;  // Current transaction amount

    CycleTypology(float minAmount, float maxAmount, int minStep, int maxStep){
        super(minAmount, maxAmount, minStep, maxStep);
    }

    /**
     * Define schedule of transaction
     * @param modelID Schedule model ID as integer
     */
    public void setParameters(int modelID){
        amount = getAmount();

        List<Account> members = alert.getMembers();  // All fraud transaction members
        int length = members.size();  // Number of members (total transactions)
        steps = new long[length];

        int totalStep = (int)(endStep - startStep + 1);
        int defaultInterval = totalStep / length;
        this.startStep = generateStartStep(defaultInterval);  //  decentralize the first transaction step

        if(modelID == FIXED_INTERVAL){  // Ordered, same interval
            long range = endStep - startStep + 1;
            if(length < range){
                this.interval = (int)(range / length); // If there is enough number of available steps, make transaction with interval
                for(int i=0; i<length; i++){
                    steps[i] = startStep + interval*i;
                }
            }else{
                this.interval = 1;
                long batch = length / range;  // Because of too many transactions, make one or more transactions per step
                for(int i=0; i<length; i++){
                    steps[i] = startStep + i/batch;
                }
            }
        }else if(modelID == RANDOM_INTERVAL || modelID == UNORDERED){  // Random interval
            this.interval = 1;
            for(int i=0; i<length; i++){
                steps[i] = getRandomStep();
            }
            if(modelID == RANDOM_INTERVAL){
                Arrays.sort(steps);  // Ordered
            }
        }
//        System.out.println("Cycle transaction steps: " + Arrays.toString(steps));
    }

    @Override
    public int getNumTransactions() {
        return alert.getMembers().size();  // The number of transactions is the same as the number of members
    }

    @Override
    public String getType() {
        return "CycleFraud";
    }

    /**
     * Create and add transactions
     * @param step Current simulation step
     */
    @Override
    public void sendTransactions(long step, Account acct) {
        int length = alert.getMembers().size();
        long alertID = alert.getAlertID();
        boolean isFraud = alert.isSAR();

        // Create cycle transactions
        for(int i=0; i<length; i++) {
            if (steps[i] == step) {
                int j = (i + 1) % length;  // i, j: index of the previous, next account
                Account src = alert.getMembers().get(i);  // The previous account
                Account dst = alert.getMembers().get(j);  // The next account
                sendTransaction(step, amount, src, dst, isFraud, alertID);

                // Update the next transaction amount
                float margin = amount * MARGIN_RATIO;
                amount = Math.max(amount - margin, minAmount);
            }
        }
    }
}