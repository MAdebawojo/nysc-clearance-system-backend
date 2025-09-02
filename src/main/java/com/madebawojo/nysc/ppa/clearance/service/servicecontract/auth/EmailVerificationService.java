package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.entity.user.User;

public interface EmailVerificationService {
    public void generateVerificationToken(User user);

    public void verifyUserByToken(String token);

    public void resendVerificationToken(String email);

//    @Scheduled(cron = "0 0 2 * * ?")
//    public void cleanupExpiredTokens();

}
