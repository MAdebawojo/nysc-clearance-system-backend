package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.entity.user.User;

public interface EmailVerificationService {
    public void generateAndSendVerificationToken(User user);

    public void verifyUserByToken(String token);

    public boolean resendVerificationToken(String email);

    public void invalidateOldTokens(User user);
//    @Scheduled(cron = "0 0 2 * * ?")
//    public void cleanupExpiredTokens();

}
