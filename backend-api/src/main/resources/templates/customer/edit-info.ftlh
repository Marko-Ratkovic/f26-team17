<#assign pageTitle = "Browse Services - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="edit-info-header">
                        <h1 class="edit-info-title">Edit contact information</h1>
                        <p class="edit-info-subtitle">Update the name and contact details on your account.</p>
                    </div>
                </div>
            </section>

            <section class="section">
                <div class="container" style="max-width: 600px;">
                    <div class="card profile-card">
                        <div class="profile-card-header">
                            <h2 class="profile-card-title">Personal information</h2>
                        </div>
                        <div class="card-content">
                        <#if error??>
                            <div class="form-error" role="alert">${error}</div>
                        </#if>
                            <form id="editPersonalInfoForm" method="post" action="/customers/edit-info">
                                <div class="form-group">
                                    <label for="fullName">Full name</label>
                                    <input type="text" id="fullName" name="name" value="${formName}" autocomplete="name" required>
                                </div>
                                <div class="form-group">
                                    <label for="email">Email address</label>
                                    <input type="email" id="email" name="email" value="${formEmail}" autocomplete="email" required>
                                    <small class="form-help">This is the address you use to sign in.</small>
                                </div>
                                <div class="form-group">
                                    <label for="phone">Phone number <span class="optional-label">(optional)</span></label>
                                    <input type="tel" id="phone" name="phone" value="${formPhone}" autocomplete="tel">
                                    <small class="form-help">Only shared with the provider of a confirmed appointment.</small>
                                </div>
                                <div class="d-flex flex-wrap gap-2">
                                    <button type="submit" class="btn settings-btn settings-btn-sm">Save changes</button>
                                    <a href="/customers/profile" class="btn btn-appt-secondary">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        
        <footer><p class="mb-0">&copy; 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>