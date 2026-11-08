<#assign pageTitle = "My Profile - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="my-profile-header">
                        <p class="profile-eyebrow">CUSTOMER ACCOUNT</p>
                        <h1 class="my-profile-title">My Profile</h1>
                        <p class="my-profile-subtitle">Manage your contact information, safety notes, saved providers, and account access.</p>
                    </div>
                </div>
            </section>

            <section class="section">
                <div class="container profile-page-content">
                    <section class="card profile-card" aria-labelledby="personal-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="personal-heading">Personal information</h2></div>
                        <div class="card-content">
                            <dl class="profile-data-grid">
                                <div><dt>Full name</dt><dd>${customer.name()}</dd></div>
                                <div><dt>Email address</dt><dd>${customer.email()}</dd></div>
                                <div><dt>Phone number</dt><dd>${customer.phoneNumber()!"Not provided"}</dd></div>
                            </dl>
                            <p class="profile-helper">Email changes require confirmation at the new address. Phone number is optional.</p>
                            <a class="btn profile-edit-btn" href="#">Edit contact information</a>
                        </div>
                    </section>

                    <section class="card profile-card" aria-labelledby="safety-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="safety-heading">Beauty &amp; safety notes</h2></div>
                        <div class="card-content">
                            <p class="profile-privacy-callout"><strong>Private information.</strong> These notes are not part of your public profile. In the live app, they are intended to be available to your provider only for a confirmed appointment.</p>
                            <dl class="profile-data-grid">
                                <div><dt>Allergy status</dt><dd>${allergyLabel}</dd></div>
                                <div><dt>Allergies</dt><dd>${allergyDetails}</dd></div>
                                <div><dt>Sensitivities</dt><dd>${sensitivities}</dd></div>
                            </dl>
                            <a class="btn profile-edit-btn" href="#">Update beauty &amp; safety notes</a>
                        </div>
                    </section>

                    <section class="card profile-card" id="saved-providers" aria-labelledby="saved-providers-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="saved-providers-heading">Saved providers</h2></div>
                        <div class="card-content">
                            <p class="profile-helper">Providers you choose to save for easier discovery later. Saving a provider does not create a booking.</p>
                            <#if savedProviders?size == 0>
                                <p class="profile-helper">You haven't saved any providers yet.</p>
                            <#else>
                                <#list savedProviders as saved>
                                    <div class="profile-provider-row">
                                        <div><h3>${saved.name()}</h3></div>
                                        <span class="profile-status">Saved</span>
                                    </div>
                                </#list>
                            </#if>
                            <a class="btn profile-edit-btn" href="/customers/browse-services">Browse providers</a>
                        </div>
                    </section>

                    <section class="card profile-card" id="your-providers" aria-labelledby="booked-providers-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="booked-providers-heading">Providers you&rsquo;ve booked</h2></div>
                        <div class="card-content">
                            <p class="profile-helper">Based on your appointment history.</p>
                            <#if bookedProviders?size == 0>
                                <p class="profile-helper">You haven't booked any appointments yet.</p>
                            <#else>
                                <#list bookedProviders as booked>
                                    <div class="profile-provider-row">
                                        <div><h3>${booked.name}</h3><p>${booked.total} appointment(s), ${booked.completed} completed</p></div>
                                    </div>
                                </#list>
                            </#if>
                            <a class="btn profile-edit-btn" href="#">View appointment history</a>
                        </div>
                    </section>

                    <section class="card profile-card" aria-labelledby="account-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="account-heading">Account &amp; privacy</h2></div>
                        <div class="card-content profile-settings-links">
                            <a href="#">Password &amp; sign-in security <span aria-hidden="true">&rarr;</span></a>
                            <a href="#">Close account <span aria-hidden="true">&rarr;</span></a>
                        </div>
                    </section>
                </div>
            </section>
        </main>

        <footer><p class="mb-0">&copy; 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>