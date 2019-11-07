//
// Note: No specific bank models are used for this fraud transaction model class.
//

package amlsim.model.aml;

import amlsim.Account;

import java.util.*;

/**
 * Bipartite transaction model
 * Some accounts send money to a different account set
 */
public class BipartiteTypology extends AMLTypology {

    @Override
    public void setParameters(int modelID) {

    }

    @Override
    public int getNumTransactions() {
        int numMembers = alert.getMembers().size();
        int numSenders = numMembers / 2;  // The first half accounts are senders
        int numReceivers = numMembers - numSenders;
        return numSenders * numReceivers;  // all-to-all
    }

    public BipartiteTypology(float minAmount, float maxAmount, int minStep, int maxStep) {
        super(minAmount, maxAmount, minStep, maxStep);
    }

    @Override
    public String getType() {
        return "BipartiteFraud";
    }

    @Override
    public void sendTransactions(long step, Account acct) {
        float amount = getAmount();  // The amount of each transaction
        List<Account> members = alert.getMembers();  // Fraud members

        int last_orig_index = members.size() / 2;  // The first half accounts are senders
        for(int i=0; i<last_orig_index; i++){
            Account orig = members.get(i);
            if(!orig.getID().equals(acct.getID())){
                continue;
            }

            for(int j=last_orig_index; j<members.size(); j++){
                Account dest = members.get(j);  // The second half accounts are receivers
                sendTransaction(step, amount, orig, dest);
            }
        }
    }
}