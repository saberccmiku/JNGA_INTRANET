package com.yskj.jnga.entity;

import java.util.List;

/**
 * Created by 63987 on 2018/4/16
 * 警员信息类
 */

public class User {
    private String RtnCode;
    private String ErrorMsg;
    private List<UserInfo> result;

    public class UserInfo{

        private String ID;
        private String POLICENUM;
        private String POLICENAME;
        private String PUNIT;
        private String JINGWUQU;
        private String ROLE;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getPOLICENUM() {
            return POLICENUM;
        }

        public void setPOLICENUM(String POLICENUM) {
            this.POLICENUM = POLICENUM;
        }

        public String getPOLICENAME() {
            return POLICENAME;
        }

        public void setPOLICENAME(String POLICENAME) {
            this.POLICENAME = POLICENAME;
        }

        public String getPUNIT() {
            return PUNIT;
        }

        public void setPUNIT(String PUNIT) {
            this.PUNIT = PUNIT;
        }

        public String getJINGWUQU() {
            return JINGWUQU;
        }

        public void setJINGWUQU(String JINGWUQU) {
            this.JINGWUQU = JINGWUQU;
        }

        public String getROLE() {
            return ROLE;
        }

        public void setROLE(String ROLE) {
            this.ROLE = ROLE;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "ID='" + ID + '\'' +
                    ", POLICENUM='" + POLICENUM + '\'' +
                    ", POLICENAME='" + POLICENAME + '\'' +
                    ", PUNIT='" + PUNIT + '\'' +
                    ", JINGWUQU='" + JINGWUQU + '\'' +
                    ", ROLE='" + ROLE + '\'' +
                    '}';
        }
    }

    public String getRtnCode() {
        return RtnCode;
    }

    public void setRtnCode(String rtnCode) {
        RtnCode = rtnCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }

    public List<UserInfo> getResult() {
        return result;
    }

    public void setResult(List<UserInfo> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "User{" +
                "RtnCode='" + RtnCode + '\'' +
                ", ErrorMsg='" + ErrorMsg + '\'' +
                ", result=" + result +
                '}';
    }
}
