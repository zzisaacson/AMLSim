import unittest 
import csv
import os
import json
import datetime
from datetime import date

conf = None
  
class SimpleTest(unittest.TestCase): 
    
    def test_num_output_accts_matches(self):         
        simName = conf["general"]["simulation_name"]
        accountFile=os.getcwd()+"/"+conf["input"]["directory"]+"/"+conf["input"]["accounts"]
        nAccts=0
        with open(accountFile, "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            indices = {name: index for index, name in enumerate(header)}
            count_idx = indices["count"]
            for row in reader:
                nAccts+= int(row[count_idx])
        with open(os.getcwd()+"/"+conf["output"]["directory"]+"/"+simName+"/"+conf["output"]["accounts"], "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            row_count = sum(1 for row in reader)
            self.assertTrue(row_count==nAccts),"Should be {nAccts}"

    def test_num_output_alerts_matches(self):         
        
        simName = conf["general"]["simulation_name"]
        alertFile=os.getcwd()+"/"+conf["input"]["directory"]+"/"+conf["input"]["alert_patterns"]
        nAccts=0
        with open(alertFile, "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            indices = {name: index for index, name in enumerate(header)}
            count_idx = indices["count"]
            for row in reader:
                nAccts+= int(row[count_idx])
        with open(os.getcwd()+"/"+conf["output"]["directory"]+"/"+simName+"/"+conf["output"]["alert_members"], "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            row_count = sum(1 for row in reader)
            self.assertTrue(row_count>=nAccts),"Should be a least {nAccts}"
    def test_correct_number_steps(self): 
        simName = conf["general"]["simulation_name"]

        inputSteps = int(conf["general"]["total_steps"])    
        transactionFile = os.getcwd()+"/"+conf["output"]["directory"]+"/"+simName+"/"+conf["output"]["transactions"]

        with open(transactionFile, "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            indices = {name: index for index, name in enumerate(header)}
            date_idx = indices["tran_timestamp"]
            d = ""
            for row in reader:
                d=row[date_idx]
            
            justDay=date.fromisoformat(str(d.split("T")[0]))
            expectedDay=date.fromisoformat(str(conf["general"]["base_date"]))+ datetime.timedelta(int(conf["general"]["total_steps"])-1)
            #print("actual day:"+str(justDay)+ " expected day:"+str(expectedDay))
            self.assertTrue(justDay==expectedDay)

    def test_alert_stat_type(self): 
        simName = conf["general"]["simulation_name"]
        alertFile = os.getcwd()+"/"+conf["input"]["directory"]+"/"+conf["input"]["alert_patterns"]

        nChiSquare=0
        minSAR, maxSAR = 0,0
        expectedMeanSAR=0

        with open(alertFile, "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            indices = {name: index for index, name in enumerate(header)}
            count_idx = indices["count"]
            sar_idx = indices["is_sar"]
            min_idx = indices["min_amount"]
            max_idx = indices["max_amount"]

            if "stat_type" in indices:
                stat_idx = indices["stat_type"]
            else:
                self.assertTrue(True)
                return

            nSAR = 0
            minSAR, maxSAR, s= 0,0,0

            nNonStandard=0
            minNonStand, maxNonStand, t =0,0,0
            for row in reader:
                if str(row[sar_idx]).lower()=="true": 
                    nSAR+=int(row[count_idx])
                    minSAR+=float(row[min_idx])*int(row[count_idx])
                    maxSAR+=float(row[max_idx])*int(row[count_idx])
                    s+=1
                    if int(row[stat_idx])==1:
                        nNonStandard+=int(row[count_idx])
                        minNonStand+=float(row[min_idx])*int(row[count_idx])
                        maxNonStand+=float(row[max_idx])*int(row[count_idx])
            if(nSAR==0):
                self.assertTrue(True)
                return
            
            minSAR /=nSAR
            maxSAR /=nSAR

            minNonStand /= nNonStandard
            maxNonStand /= nNonStandard

            nonStandProportion = nNonStandard/nSAR
            
            expectedMeanSAR = ((minSAR+maxSAR)/2)*(1-nonStandProportion) + ((5*minNonStand+maxNonStand)/6)*nonStandProportion

        transactionFile = os.getcwd()+"/"+conf["output"]["directory"]+"/"+simName+"/"+conf["output"]["transactions"]
        actualMeanSAR=0
        with open(transactionFile, "r") as rf:
            reader = csv.reader(rf)
            header = next(reader)
            indices = {name: index for index, name in enumerate(header)}
            sar_idx = indices["is_sar"]
            amount_idx = indices["base_amt"]

            n=0
            for row in reader:
                if str(row[sar_idx]).lower()=="true":
                    #print(row[amount_idx])
                    actualMeanSAR+=float(row[amount_idx])
                    n+=1
                    
            actualMeanSAR/=n

        self.assertTrue(abs(actualMeanSAR-expectedMeanSAR)<(maxSAR-minSAR)/4)
  
if __name__ == '__main__': 
    confFile="conf2.json"
    with open("Tests/"+confFile, "r") as rf:
        conf = json.load(rf)

    os.system("python3 scripts/transaction_graph_generator.py Tests/"+confFile)
    os.system("sh scripts/build_AMLSim.sh")
    os.system("sh scripts/run_AMLSim.sh Tests/"+confFile)
    os.system("python3 scripts/convert_logs.py Tests/"+confFile)
    unittest.main() 

    os.system("sh Tests/clean_logs.sh")