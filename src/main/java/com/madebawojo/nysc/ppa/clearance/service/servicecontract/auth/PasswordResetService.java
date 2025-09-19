package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordResetToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import org.springframework.scheduling.annotation.Scheduled;

public interface PasswordResetService {
    public void requestPasswordReset(String email);
    public PasswordResetToken validateToken(String token);
    public void resetPassword(String token, String newPassword);
    public void invalidateOldTokens(User user);
    @Scheduled(cron = "0 0 0 * * ?") // once a day at midnight
    public void cleanUpExpiredTokens();
}
