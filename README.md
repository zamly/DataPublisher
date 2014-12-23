DataPublisher
=============
1. Copy the source into the <CEP_HOME>/samples/producers/. 
2. Open terminal, go to <CEP_HOME>/samples/producers/DataPublishser and run ant from there with all the parameters specified below. 
	  Please make sure to pass the required parameters.
	  NOTE: -DcepPort and -DbamPort should be the tcp thrift ports.
	   
   To configure cephost, cepport, cepUsername, cepPassword, use -DcepHost=xxxx -DcepPort=xxxx -DcepUsername=xxxx -DcepPassword=xxxx and
   To configure bamhost, bamport, bamUsername, bamPassword and No. of events, use -DbamHost=xxxx -DbamPort=xxxx -DbamUsername=xxxx -DbamPassword=xxxx -Devents=xx and
   For example : ant -DbamHost=”10.100.10.186” -DbamPort=”7611” -Devents=10
