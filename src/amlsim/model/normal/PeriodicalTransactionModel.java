package amlsim.model.normal;

import amlsim.Account;
import amlsim.model.AbstractTransactionModel;


/**
 * Send money to neighbors periodically
 */
public class PeriodicalTransactionModel extends AbstractTransactionModel {

    private int index = 0;

    public void setParameters(int interval, float balance, long start, long end){
        super.setParameters(interval, balance, start, end);
        if(this.startStep < 0){  // decentralize the first transaction step
            this.startStep = generateStartStep(interval);
        }
    }

    @Override
    public String getType() {
        return "Periodical";
    }

    private boolean isValidStep(long step){
        return (step - startStep) % interval == 0;
    }

    @Override
    public void sendTransaction(long step) {
        if(!isValidStep(step) || this.account.getBeneList().isEmpty()){
            return;
        }
        int numDests = this.account.getBeneList().size();
        if(index >= numDests){
            index = 0;
        }

        int totalCount = getNumberOfTransactions();  // Total number of transactions
        int eachCount = (numDests < totalCount) ? 1 : numDests / totalCount;

        for(int i=0; i<eachCount; i++) {

            float amount = 0;  // this.balance;
            switch(this.account.statType())
            {
                case 0: amount = getTransactionAmount(); break;
                case 1: amount = this.account.getChiSquaredAmount(); break;
                default: System.err.println("Unrecognized stat type");break;
            }

            Account dest = this.account.getBeneList().get(index);
            this.sendTransaction(step, amount, dest);
            index++;
            if(index >= numDests) break;
        }
        index = 0;
    }
}
