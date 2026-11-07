<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Get Started - Glow Up</title>
    <link rel="stylesheet" href="styles.css">
    <link rel="stylesheet" href="register.css">
</head>

<body>
    <div class="register-container">
        <#include "/shared/nav.ftlh">
            <main class="login-page section">
                <div class="login-card signup-card">
                    <div class="login-card-intro">
                        <p class="login-eyebrow">JOIN GLOW UP</p>
                        <h1>Find your place in our beauty community.</h1>
                        <p>Whether you are booking your next appointment or growing your beauty business, Glow Up helps
                            you connect with the right people.</p>
                    </div>

                    <div class="login-form-wrap">
                        <h2>Create your account</h2>
                        <p class="login-form-subtitle">Choose an account type to get started.</p>

                        <#-- Shows the message when registration fails -->
                        <#if error??>
                            <div role="alert" style="color:#ff8a8a; background:rgba(255,80,80,0.12); border:1px solid #ff8a8a; border-radius:8px; padding:10px 14px; margin-bottom:16px;">${error}</div>
                        </#if>

                        <form method="post" action="/register" id="registerForm">
                            <fieldset class="role-selector">
                                <legend>I am signing up as a</legend>
                                <div class="role-options">
                                    <label class="role-option">
                                        <input type="radio" name="signup-role" value="customer"
                                            checked>
                                        <span>
                                            <strong>Customer</strong>
                                            <small>Discover and book beauty services</small>
                                        </span>
                                    </label>
                                    <label class="role-option">
                                        <input type="radio" name="signup-role" value="provider">
                                        <span>
                                            <strong>Provider</strong>
                                            <small>Offer services and manage clients</small>
                                        </span>
                                    </label>
                                </div>
                            </fieldset>

                            <div class="form-group">
                                <label for="full-name">Full name</label>
                                <input type="text" id="full-name" name="name" placeholder="Your full name"
                                    value="${formName!}" autocomplete="name" required>
                            </div>

                            <div class="form-group">
                                <label for="signup-email">Email address</label>
                                <input type="email" id="signup-email" name="email" placeholder="you@example.com"
                                    value="${formEmail!}" autocomplete="email" required>
                            </div>

                            <div class="form-group">
                                <label for="signup-password">Password</label>
                                <input type="password" id="signup-password" name="password"
                                    value="${formPassword!}" placeholder="Create a password" autocomplete="new-password" minlength="8" required>
                                <small class="form-help">Use at least 8 characters.</small>
                            </div>

                            <div class="form-group">
                                <label for="confirm-password">Confirm password</label>
                                <input type="password" id="confirm-password" name="confirm-password"
                                    value="${formConfirmPassword!}" placeholder="Re-enter your password" autocomplete="new-password" minlength="8"
                                    required>
                            </div>

                            <div id="customer-fields" class="signup-role-fields">
                                <div class="form-group">
                                    <label for="customer-phone">Phone number <span
                                            class="optional-label">(optional)</span></label>
                                    <input type="tel" id="customer-phone" name="customer-phone"
                                        value="${formPhone!}" placeholder="(555) 555-5555" autocomplete="tel">
                                </div>
                            </div>

                            <div id="provider-fields" class="signup-role-fields">
                                <div class="signup-section-heading">
                                    <h3>Tell clients about your services</h3>
                                    <p>This information helps customers find the right provider.</p>
                                </div>
                                <div class="signup-form-grid">
                                    <div class="form-group">
                                        <label for="business-name">Public name</label>
                                        <input type="text" id="business-name" name="businessName"
                                            placeholder="Name customers will see">
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-experience">Years of experience</label>
                                        <input type="number" id="provider-experience" name="yearsOfExperience"
                                            placeholder="e.g. 5" min="0" max="70" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-phone">Phone</label>
                                        <input type="tel" id="provider-phone" name="phoneNumber"
                                            placeholder="(555) 555-5555" autocomplete="tel">
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-street">Street address</label>
                                        <input type="text" id="provider-street" name="addressLine"
                                            placeholder="123 Elm St, Suite 4" autocomplete="street-address" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-city">City</label>
                                        <input type="text" id="provider-city" name="city" placeholder="Greensboro"
                                            autocomplete="address-level2" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-state">State</label>
                                        <input type="text" id="provider-state" name="state" placeholder="NC"
                                            maxlength="2" autocomplete="address-level1" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="provider-zip">ZIP code</label>
                                        <input type="text" id="provider-zip" name="zipcode" placeholder="27401"
                                            inputmode="numeric" autocomplete="postal-code" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="certification">Certifications (optional)</label>
                                        <input type="text" id="certification" name="certifications"
                                            placeholder="e.g. State cosmetology license">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="provider-bio">Professional bio</label>
                                    <textarea id="provider-bio" name="bio" rows="4"
                                        placeholder="Share your experience, specialties, and what clients can expect."></textarea>
                                    <small class="form-help">A short introduction helps customers feel confident booking
                                        with you.</small>
                                </div>

                                <div class="signup-section-heading availability-heading">
                                    <h3>Set your availability</h3>
                                    <p>Pick the days you work and your usual hours. You can set different hours for each
                                        day later on your Availability page.</p>
                                </div>
                                <div class="availability-panel">
                                    <div class="availability-group">
                                        <h4>Days you are available</h4>
                                        <div class="availability-options">
                                            <#list weekdays as day>
                                                <label class="availability-option">
                                                    <input type="checkbox" name="availableDays" value="${day}">
                                                    <span>${day?lower_case?cap_first}</span>
                                                </label>
                                            </#list>
                                        </div>
                                    </div>
                                    <div class="availability-group">
                                        <h4>Opening hours on those days</h4>
                                        <div class="availability-presets" role="group"
                                            aria-label="Quick fill opening hours">
                                            <button type="button" class="btn btn-sm btn-appt-secondary"
                                                data-open="09:00" data-close="12:00">Morning</button>
                                            <button type="button" class="btn btn-sm btn-appt-secondary"
                                                data-open="12:00" data-close="17:00">Afternoon</button>
                                            <button type="button" class="btn btn-sm btn-appt-secondary"
                                                data-open="17:00" data-close="21:00">Evening</button>
                                            <button type="button" class="btn btn-sm btn-appt-secondary"
                                                data-open="09:00" data-close="17:00">Full day</button>
                                        </div>
                                        <div class="availability-hours">
                                            <div>
                                                <label for="provider-open-time">Opens at</label>
                                                <input type="time" id="provider-open-time" name="openTime" value="09:00"
                                                    step="900">
                                            </div>
                                            <div>
                                                <label for="provider-close-time">Closes at</label>
                                                <input type="time" id="provider-close-time" name="closeTime"
                                                    value="17:00" step="900">
                                            </div>
                                        </div>
                                        <small class="form-help">The buttons only fill in the times; you can type your
                                            own. Days you don't pick are saved as closed.</small>
                                    </div>
                                </div>
                            </div>

                            <details class="signup-policy">
                                <summary>Review the Terms &amp; Privacy Policy</summary>
                                <div class="signup-policy-content">
                                    <p>Before creating an account, please review how Glow Up expects customers and
                                        providers
                                        to use the platform and how account information is handled.</p>
                                    <ul>
                                        <li>Keep account, service, pricing, and booking information accurate.</li>
                                        <li>Communicate respectfully and do not post fraudulent, harassing, or abusive
                                            content.</li>
                                        <li>New provider service listings are reviewed before they become visible to
                                            customers.</li>
                                        <li>Reviews are for completed appointments and publish when submitted; reported
                                            content
                                            may be reviewed under the community guidelines.</li>
                                    </ul>
                                    <p class="signup-policy-links">
                                        <a href="policies/terms.html" target="_blank" rel="noopener">Read the full Terms
                                            of Service</a>
                                        <a href="policies/privacy.html" target="_blank" rel="noopener">Read the Privacy
                                            Policy</a>
                                    </p>
                                </div>
                            </details>

                            <div class="register-checkbox">
                                <input type="checkbox" id="terms" name="termsAccepted" value="true" required>
                                <label for="terms">
                                    I have read and agree to the
                                    <a href="policies/terms.html" target="_blank" rel="noopener">Terms of Service</a>
                                    and <a href="policies/privacy.html" target="_blank" rel="noopener">Privacy
                                        Policy</a>.
                                </label>
                            </div>

                            <div class="register-button">
                                <button type="submit" class="btn-login">Create account</button>
                            </div>
                        </form>

                        <p class="login-register-prompt signup-existing">
                            Already have an account?
                            <a href="/login" class="login-link register-login-link">Log in</a>
                        </p>

                        <div class="login-role-links">
                            <span>View demo dashboard:</span>
                            <a href="customer/dashboard.html">Customer demo</a>
                            <a href="provider/dashboard.html">Provider demo</a>
                            <a href="admin/dashboard.html">Admin demo</a>
                        </div>
                    </div>
                </div>
            </main>

            <footer>
                <p>© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p>
            </footer>
    </div>

    <script>
        // Quick-fill buttons for the provider's opening hours (they only set the two time fields).
        document.querySelectorAll('.availability-presets [data-open]').forEach(function (button) {
            button.addEventListener('click', function () {
                document.getElementById('provider-open-time').value = button.dataset.open;
                document.getElementById('provider-close-time').value = button.dataset.close;
            });
        });

        const form = document.getElementById('registerForm');
        const role= document.querySelectorAll('input[name="signup-role"]');
        role.forEach(radio => {
            radio.addEventListener('change', (event) => {
                if (event.target.checked) {
                    form.action = '/' + event.target.value + '/register';
                }
            });
        });

        // Disable the fields of the role that is not selected, so hidden required fields cannot block the submit
        function syncRoleFields() {
            const selected = document.querySelector('input[name="signup-role"]:checked').value;
            document.querySelectorAll('#provider-fields input, #provider-fields select, #provider-fields textarea')
                .forEach(el => el.disabled = selected !== 'provider');
            document.querySelectorAll('#customer-fields input')
                .forEach(el => el.disabled = selected !== 'customer');
        }
        role.forEach(radio => radio.addEventListener('change', syncRoleFields));
        syncRoleFields();

        const dayBoxes = document.querySelectorAll('input[name="availableDays"]');
        function checkDays() {
            const anyChecked = Array.from(dayBoxes).some(box => box.checked);
            dayBoxes[0].setCustomValidity(
                anyChecked ? '' : 'Select at least one available day'
            );
        }
        dayBoxes.forEach(box => box.addEventListener('change', checkDays));
        checkDays();
    </script>
</body>

</html>