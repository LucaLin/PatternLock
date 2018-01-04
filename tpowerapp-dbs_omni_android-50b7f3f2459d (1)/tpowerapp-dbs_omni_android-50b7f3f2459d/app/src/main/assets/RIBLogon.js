//test=function() {
// return "xxx";
function RIBLogon(){

//	kony.print("Method : RIBLogon");
	var exponentHexStr = "10001";
	//var exponentHexStr = "3";
    var modulusHexStr;



	this.setKeyValue =function(publickey){
//		kony.print("here in setKeyValue :"+publickey);
		modulusHexStr = publickey;
		
	}

	this.encyptPwd = function(pwd,rdmValue) {
		
//		kony.print("here in encrypt :"+pwd);
		//var pwd = getPwd();
		if (pwd == null){
			showAlerts("mb.pwd_empty");
			return;
		
		}
		var encBuff ;
		//format pinverify
		var dataBytes = buildPKCS15BlockForPinVerify(pwd,rdmValue);
//console.log("dataBytes hexstr pinverify = "+Util.toHexString(dataBytes));
//console.log("dataBytes hexstr pinverify len = "+Util.toHexString(dataBytes).length);
		var rsa = null;
		
		 try{
			 rsa = new RSAKey();
		 } catch (e){throw "RSA constructor error when pwd verify";}
//console.log("modulusHexStr hexstr pinverify = "+modulusHexStr);
//console.log("exponentHexStr hexstr pinverify = "+exponentHexStr);
		rsa.setPublic(modulusHexStr, exponentHexStr);
		
		try {
			
  			
			encBuff = rsa.encryptNativeBytes(dataBytes);
//			kony.print("pin after encryption :"+encBuff);
			
		} catch (e) {
			throw "RSA encrypt error when pin verify"; // errorcode"100" = get exception when encrypt data
		}
	
		return encBuff;	
		
		
	}

	this.encyptPwdChange = function(oldPwd, newPwd,rdm) {
		//var pwd = getPwd();
		if (oldPwd == null || newPwd == null){
			showAlerts("mb.pwd_empty");
			return;
		
		}
			
		var encBuff ;
		//format pinverify
		var dataBytes = buildPKCS15BlockForPinChange(oldPwd, newPwd,rdm);
//console.log("dataBytes hexstr pinchage = "+Util.toHexString(dataBytes));
//console.log("dataBytes hexstr pinchage len= "+Util.toHexString(dataBytes).length);
		var rsa = null;
		
		 try{
			 rsa = new RSAKey();
		 } catch (e){throw "RSA constructor error when pin changing";}

		 rsa.setPublic(modulusHexStr, exponentHexStr);
		
		try {
			
  			
			encBuff = rsa.encryptNativeBytes(dataBytes);
			
		} catch (e) {
			throw "RSA encrypt error when pin changing"; // errorcode"100" = get exception when encrypt data
		}
	
		return encBuff;
		
	}

/**
    * This builds a byte array that is in accordance with PKCS#1 v1.5 standard
    * according to section 10.1 of Group Internet Banking System (GIB)
    * Communication Message Specification for a PIN Change operation
    * 
    * @param oldPin
    * @param newPin
    * @param random
    * @return
    * @throws UnsupportedEncodingException
    */
   function buildPKCS15BlockForPinChange(oldPin, newPin,random){

      if (random.length != 16) {
         showAlerts("mb.random_no_minlength");
		 return;
      }
      if (oldPin.length > 30) {
         showAlerts("mb.pin_minlenght");
		 return;
      }
      if (newPin.length > 30) {
         showAlerts("mb.newpin_minlenght");
		 return;
      }

      // block size is 128 bytes according to spec
      var bytes = new Array();

      // convert the PIN to bytes from string
      var oldPINBytes = Util.getByteArray(oldPin);

      // generate the 30 byte password portion
      var oldPasswordBytes = new Array(30);
      for (var i = 0; i < 30; i++) {
         if (i < oldPINBytes.length)
            oldPasswordBytes[i] = oldPINBytes[i];
         else
            oldPasswordBytes[i] =  0xFF;
      }

      // convert the PIN to bytes from string
      var newPINBytes = Util.getByteArray(newPin);

      // generate the 30 byte password portion
      var newPasswordBytes = new Array(30);
      for (var i = 0; i < 30; i++) {
         if (i < newPINBytes.length)
            newPasswordBytes[i] = newPINBytes[i];
         else
            newPasswordBytes[i] =  0xFF;
      }

      // convert the random number to bytes from string. Random number is
      // expected to be in hes format
      var randomBytes = Util.fromHexString(random);

      var zeros = 128 - randomBytes.length - newPasswordBytes.length - oldPasswordBytes.length;
	  var bytesPad = Util.randomBytes(zeros);  //this is for random bytes 
	  for (var i = 0; i < zeros; i++) {
         if (bytesPad[i] == 0x00) {
            // arbitrarily replace with 0x28 for now
            bytesPad[i] = 0x28;
         }
      }
	  bytesPad[0]=0x00;
	  bytesPad[1]=0x02;
	  bytesPad[10]=0x00;

	  bytes = bytesPad.concat(randomBytes);
	  bytes = bytes.concat(newPasswordBytes);
	  bytes = bytes.concat(oldPasswordBytes);
	 
      return bytes;
   }

	 /**
    * This method builds a byte array that is in accordance with PKCS#1 v1.5
    * standard according to section 10.1 of Group Internet Banking System (GIB)
    * Communication Message Specification for PIN verify operation
    * 
    * @param pin
    *           The users pin
    * @param random
    *           The random number as supplied from the host
    * @return a 128 byte array corresponding to the PKCS block
    * @throws UnsupportedEncodingException
    *            if ISO-8859-1 encoding is not supported
    */
   function buildPKCS15BlockForPinVerify( pin,random){
      
      if (pin.length > 30) {
         showAlerts("mb.pinlenght");
		 reurn;
      }

      // block size is 128 bytes according to spec
     var bytes = new Array();

      // convert the PIN to bytes from string
      var PINBytes = Util.getByteArray(pin);

      // now generate the 30 byte password portion
      var passwordBytes = new Array(30);
      for (var i = 0; i < 30; i++) {
         if (i < PINBytes.length)
            passwordBytes[i] = PINBytes[i];
         else
            passwordBytes[i] = 0xFF;
      }

      // convert the random number to bytes from string
	 
      var RandomBytes = Util.fromHexString(random);
	 
	 
	  var zeros = 128 - RandomBytes.length - passwordBytes.length;
	  var bytesPad = Util.randomBytes(zeros);  //this is for random bytes 
	  for (var i = 0; i < zeros; i++) {
         if (bytesPad[i] == 0x00) {
            // arbitrarily replace with 0x27 for now
            bytesPad[i] = 0x27;
         }
      }
	  
	  bytesPad[0]=0x00;
	  bytesPad[1]=0x02;
	  bytesPad[10]=0x00;

	  bytes = bytesPad.concat(RandomBytes);
	  bytes = bytes.concat(passwordBytes);


      return bytes;
   }



}