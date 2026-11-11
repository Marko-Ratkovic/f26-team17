<#assign pageTitle="Manage Appointments - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section"><div class="container"><div class="edit-info-header mb-0">
                <h1 class="edit-info-title">Appointment details</h1>
                <p class="edit-info-subtitle">Customer and service information for the associated booking.</p>
            </div></div></section>
            <section class="section">
                <div class="container provider-profile-content">
                    <p class="provider-prototype-note" role="note">Privacy preview: the live app should authorize this view only for the provider assigned to a confirmed appointment. This static page cannot enforce identity or access rules.</p>
                    <section class="card provider-settings-card " id="confirmed-booking" aria-labelledby="booking-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="booking-heading">Confirmed appointment</h2></div>
                        <div class="card-content">
                            <dl class="profile-data-grid">
                                <div><dt>Customer</dt><dd>Laura Johnson</dd></div>
                                <div><dt>Status</dt><dd>Confirmed</dd></div>
                                <div><dt>Service</dt><dd>Full set acrylic nails</dd></div>
                                <div><dt>Date &amp; time</dt><dd>Friday, Oct 9, 2026 · 9:00 AM</dd></div>
                                <div><dt>Duration</dt><dd>30 minutes</dd></div>
                                <div><dt>Price</dt><dd>$75</dd></div>
                            </dl>
                        </div>
                    </section>
                    <section class="card provider-settings-card" aria-labelledby="contact-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="contact-heading">Appointment contact</h2></div>
                        <div class="card-content">
                            <p class="profile-helper">Use this contact information only to coordinate this booking. It is not public profile information.</p>
                            <dl class="profile-data-grid">
                                <div><dt>Email</dt><dd>laura@example.com</dd></div>
                                <div><dt>Phone</dt><dd>+1 (336) 555-0142</dd></div>
                            </dl>
                        </div>
                    </section>
                    <section class="card provider-settings-card" aria-labelledby="safety-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="safety-heading">Private beauty &amp; safety notes</h2></div>
                        <div class="card-content">
                            <p class="profile-privacy-callout"><strong>Appointment-specific information.</strong> The live service should expose these notes only to the provider assigned to this confirmed appointment. Do not copy notes into general customer lists or use them for marketing.</p>
                            <dl class="profile-data-grid">
                                <div><dt>Allergy status</dt><dd>No known allergies reported</dd></div>
                                <div><dt>Sensitivities</dt><dd>Not provided</dd></div>
                            </dl>
                            <p class="profile-helper mb-0">These customer-entered notes are not medical advice. Ask the customer if clarification is needed before proceeding.</p>
                        </div>
                    </section>
                    <a href="appointments.html#confirmed" class="btn btn-appt-secondary">Back to confirmed appointments</a>
                </div>
            </section>
        </main>
        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>