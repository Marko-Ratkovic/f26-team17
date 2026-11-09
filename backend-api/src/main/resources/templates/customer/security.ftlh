<#assign pageTitle = "Browse Services - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="edit-info-header">
                        <h1 class="edit-info-title">Password &amp; sign-in security</h1>
                        <p class="edit-info-subtitle">Change the password used to sign in to GlowUp.</p>
                    </div>
                </div>
            </section>
            <section class="section">
                <div class="container" style="max-width: 600px;">
                    <div class="card profile-card">
                        <div class="profile-card-header"><h2 class="profile-card-title">Change password</h2></div>
                        <div class="card-content">
                           <#if error??>
                                <div class="form-error" role="alert">${error}</div>
                            </#if>
                            <form method="post" action="/customers/security">
                                <div class="form-group">
                                    <label for="currentPassword">Current password</label>
                                    <input type="password" id="currentPassword" name="currentPassword" autocomplete="current-password" required>
                                </div>
                                <div class="form-group">
                                    <label for="newPassword">New password</label>
                                    <input type="password" id="newPassword" name="newPassword" autocomplete="new-password" minlength="8" maxlength="72" required>
                                    <small class="form-help">Use 8 to 72 characters.</small>
                                </div>
                                <div class="form-group">
                                    <label for="confirmPassword">Confirm new password</label>
                                    <input type="password" id="confirmPassword" name="confirmPassword" autocomplete="new-password" minlength="8" maxlength="72" required>
                                </div>
                                <div class="d-flex flex-wrap gap-2">
                                    <button type="submit" class="btn settings-btn settings-btn-sm">Update password</button>
                                    <a href="/customers/profile" class="btn btn-appt-secondary">Cancel</a>
                                </div>
                            </form>
                            <p class="profile-helper mt-3 mb-0">Forgot your password? Use the password-reset link on the sign-in page.</p>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        
        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>