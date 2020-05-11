package amlsim.model.normal;

import amlsim.Account;
import amlsim.model.AbstractTransactionModel;

import java.util.*;

/**
 * Send money received from an account to another account in a similar way
 */
public class ForwardTransactionModel extends AbstractTransactionModel {
    private int index = 0;

    public void setParameters(int interval, float balance, long start, long end){
        super.setParameters(interval, balance, start, end);
        if(this.startStep < 0){  // decentralize the first transaction step
            this.startStep = generateStartStep(interval);
        }
    }

    @Override
    public String getType() {
        return "Forward";
    }

    @Override
    public void sendTransaction(long step) {

        

        float amount = 0;  // this.balance;

        switch(this.account.statType())
        {
            case 0: amount = getTransactionAmount(); break;
            case 1: amount = this.account.getChiSquaredAmount(); break;
            default: System.err.println("Unrecognized stat type");break;
        }
        List<Account> dests = this.account.getBeneList();
        int numDests = dests.size();
        if(numDests == 0){
            return;
        }
        if((step - startStep) % interval != 0){
            return;
        }

        if(index >= numDests){
            index = 0;
        }
        Account dest = dests.get(index);
        this.sendTransaction(step, amount, dest);
        index++;
    }
}
