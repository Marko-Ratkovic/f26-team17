<#assign pageTitle="Provider Edit Profile - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="edit-info-header">
                        <h1 class="edit-info-title">Edit provider details</h1>
                        <p class="edit-info-subtitle">Manage account contact details and the information customers see.
                        </p>
                    </div>
                </div>
            </section>
            <section class="section">
                <div class="container" style="max-width: 760px;">
                    <div class="card profile-card">
                        <div class="profile-card-header">
                            <h2 class="profile-card-title">Provider information</h2>
                        </div>
                        <div class="card-content">
                            <form method="post">
                                <fieldset class="provider-form-section">
                                    <legend>Account contact</legend>
                                    <div class="form-group"><label for="fullName">Full name</label><input id="fullName" type="text"
                                            name="fullName" value="${provider.name()}" autocomplete="name" required>
                                    </div>
                                    <div class="form-group"><label for="email">Email address</label><input id="email"
                                            name="email" type="email" value="${provider.email()}" autocomplete="email"
                                            required><small class="form-help">A new address should not replace this one
                                            until it is verified.</small></div>
                                    <div class="form-group"><label for="phone">Phone number <span
                                                class="optional-label">(optional)</span></label><input id="phone"
                                            name="phone" type="tel" value="${provider.phone()!''}" autocomplete="tel"><small
                                            class="form-help">Only share contact details with customers when needed for
                                            an associated booking.</small></div>
                                </fieldset>
                                <fieldset class="provider-form-section">
                                    <legend>Public professional profile</legend>
                                    <div class="form-group"><label for="displayName">Business or display
                                            name</label><input id="displayName" type="text" name="displayName"
                                            value="${provider.businessName()}" maxlength="100" required></div>
                                    <div class="form-group"><label for="experience">Years of experience</label><input
                                            id="experience" name="experienceYears" type="number" value="${provider.yearsOfExperience()}" min="0"
                                            max="70" required></div>
                                    <div class="form-group"><label for="bio">Professional bio</label><textarea id="bio"
                                            name="bio" rows="4" maxlength="1000"
                                            required>${provider.bio()!''}</textarea>
                                    </div>
                                    <div class="row">
                                        <div class="col">
                                            <div class="form-group">
                                                <label for="provider-street">Street address</label>
                                                <input type="text" id="provider-street" name="provider-street"
                                                    value="${provider.addressLine()}" autocomplete="street-address">
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="form-group">
                                                <label for="provider-city">City</label>
                                                <input type="text" id="provider-city" name="provider-city"
                                                    value="${provider.city()}" autocomplete="address-level2">
                                            </div>
                                        </div>

                                    </div>
                                    <div class="row">
                                        <div class="col">
                                            <div class="form-group">
                                                <label for="provider-state">State</label>
                                                <input type="text" id="provider-state" name="provider-state"
                                                    value="${provider.state()}" maxlength="2" autocomplete="address-level1">
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="form-group">
                                                <label for="provider-zip">ZIP code</label>
                                                <input type="text" id="provider-zip" name="provider-zip"
                                                    value="${provider.zipcode()}" inputmode="numeric" autocomplete="postal-code">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group"><label for="credentials">Certifications <span
                                                class="optional-label">(optional)</span></label><textarea
                                            id="credentials" name="credentials" rows="3" maxlength="1000"
                                            placeholder="e.g. Licensed cosmetologist, NC Board">${provider.certifications()!''}</textarea><small
                                            class="form-help">Shown on your profile as self-reported.</small></div>
                                </fieldset>
                                <div class="d-flex flex-wrap gap-2">
                                    <button type="submit" class="btn btn-appt-primary">Save
                                        details</button>
                                    <a href="/providers/profile" class="btn btn-appt-secondary">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        <footer>
            <p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p>
        </footer>
    </div>
</body>

</html>