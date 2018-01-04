function encpty(pKey, rKey, pCode) {
    try {
        const logon = new RIBLogon();
        logon.setKeyValue(pKey);
        return logon.encyptPwd(pCode, rKey);
    } catch (error) {
        //return error.message;
        return 'Encryption failed';
    }
};