package com.luv2code.doan.bean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePassword {
    @NotBlank(message = "Mật khẩu cũ không được bỏ trống!")
    @Size(min = 8, max = 100, message = "Mật khẩu cũ phải từ 8 kí tự trở lên!")
    private String oldPass;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống!")
    @Size(min = 8, max = 100, message = "Mật khẩu mới phải từ 8 kí tự trở lên!")
    private String newPass;

    @NotBlank(message = "Mật khẩu xác nhận không được bỏ trống!")
    @Size(min = 8, max = 100, message = "Mật khẩu xác nhận phải từ 8 kí tự trở lên!")
    private String confirmPass;

    public ChangePassword(String oldPass, String newPass, String confirmPass) {
        this.oldPass = oldPass;
        this.newPass = newPass;
        this.confirmPass = confirmPass;
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
