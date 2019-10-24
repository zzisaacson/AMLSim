"""
Compute statistical information from generated transaction networks
"""
import sys
import json
import networkx as nx

from analytics.network_loader import BaseGraphLoader, ResultGraphLoader


class GraphStat:

    def __init__(self, conf_json):
        with open(conf_json, "r") as rf:
            self.conf = json.load(rf)
        output_conf = self.conf["output"]

        # Create a transaction graph from output files
        self.g = nx.MultiDiGraph()
        acct_file = output_conf["accounts"]
        tx_file = output_conf["transactions"]
        alert_acct_file = output_conf["alert_members"]
        alert_tx_file = output_conf["alert_transactions"]


if __name__ == "__main__":
    argv = sys.argv
    if len(argv) < 2:
        print("Usage: python3 %s [ConfJSON]" % argv[0])
        exit(1)

    conf_json = argv[1]

    # Base transaction network (as input of the simulator) analytics
    bgl = BaseGraphLoader(conf_json)

    # Final transaction network analysis
    rgl = ResultGraphLoader(conf_json)
