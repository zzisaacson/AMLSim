package amlsim.model.normal;

import amlsim.model.AbstractTransactionModel;
import java.util.*;
import amlsim.*;

public class GatherScatterTransactionModel extends AbstractTransactionModel {

    public void setParameters(int interval, float balance, long start, long end){
        super.setParameters(interval, balance, start, end);
        if(this.startStep < 0){  // decentralize the first transaction step
            this.startStep = generateStartStep(2*interval);
        }
    }
    
    @Override
    public void sendTransaction(long step){

        List<Account> benes = this.account.getOrigList();
        if(isPayPeriodStep(step)){
            for(Account bene : benes){
                
                float amount = 0;
                switch(this.account.statType())
                {
                    case 0: amount = getTransactionAmount(); break;
                    case 1: amount = this.account.getChiSquaredAmount(); break;
                    case 2: amount = this.account.getNormalDistAmount(); break;
                    default: System.err.println("Unrecognized stat type");break;
                }
               // System.out.println("PAYING PAYCHECK $"+amount);
                sendTransaction(step, amount, this.account, bene);
            }
        }

        
        List<Account> origs = this.account.getOrigList();  // Sender accounts
        for(Account orig : origs){
            if((int)(Math.random()*interval)%interval==0){
                float amount = 0;
                switch(orig.statType())
                {
                    case 0: amount = orig.getModel().getTransactionAmount(); break;
                    case 1: amount = orig.getChiSquaredAmount(); break;
                    case 2: amount = orig.getNormalDistAmount(); break;
                    default: System.err.println("Unrecognized stat type");break;
                }

                if(this.account.acctType()=='M'){
                    amount/=5;
                }

                sendTransaction(step, amount, orig, this.account);
            }
        }
    }

    private boolean isPayPeriodStep(long step){
        return (step - startStep) % (2 *interval) == 0;
    }
    @Override
    public String getType() {
        return "GatherScatter";
    }
    
}