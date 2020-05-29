module.exports = function(app,db){
	app.post('/simulator',(req,res)=>{
		
		var numResults =10;

		const http = require('http');
		const url = require('url');



		if(url.parse(req.url,true).query.num_transactions!=null){
			numResults = Number(url.parse(req.url,true).query.num_transactions)
		}
		require("child_process").execSync("ls", {stdio: 'inherit'});
		require("child_process").execSync("sh scripts/clean_logs.sh", {stdio: 'inherit'});
		require("child_process").execSync("python3 scripts/transaction_graph_generator.py conf.json", {stdio: 'inherit'});
		require("child_process").execSync("sh scripts/build_AMLSim.sh", {stdio: 'inherit'});
		require("child_process").execSync("sh scripts/run_AMLSim.sh conf.json", {stdio: 'inherit'});
		require("child_process").execSync("python3 scripts/convert_logs.py conf.json", {stdio: 'inherit'});

		const neatCsv = require('neat-csv');

		const fs = require('fs')


		fs.readFile('./outputs/test1/transactions.csv', async (err, data) => {
		  if (err) {
		    console.error(err)
		    return
		  }
		  var transactions = await neatCsv(data),
	      filteredArray = transactions.filter(
	      function (obj) {
	          return Number(obj.tran_id) <=numResults;
	      })

		res.send(await (filteredArray));
	  	});

	})
}