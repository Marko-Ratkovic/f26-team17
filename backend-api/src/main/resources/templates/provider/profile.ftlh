<#assign pageTitle="Provider Profile - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="provider-profile-hero mb-3 d-flex justify-content-between">
                        <div>
                            <p class="provider-eyebrow">PROVIDER ACCOUNT</p>
                            <h1>${provider.businessName()}</h1>
                            <#if provider?? && provider.accountStatus?? && provider.accountStatus().name() == "ACTIVE">
                                <span class="provider-visibility-badge">Profile visible to customers</span>
                            <#else>
                                <span class="provider-visibility-badge" style="background: #fff2d9; color: #704600;border: 1px solid #704600">Profile is hidden from customers</span>
                            </#if>
                        </div>
                        <div>
                            <#if provider.imagePath()?has_content>
                                <img src="/images/providers/${provider.imagePath()}" alt="${provider.name()}" style="width:120px;height:120px; border-radius: 5px;">
                            <#else>
                                <img src="/images/providers/provider-placeholder.svg" alt="Default Placeholder" style="width:120px;height:120px; border-radius: 5px;">
                            </#if>
                        </div>
                    </div>
                    
                </div>
            </section>
                    
            <section class="section">
                <div class="container provider-profile-content">
                    <#if success??>
                        <div class="form-success" role="status">${success}</div>
                    </#if>
                    <section class="card provider-settings-card" aria-labelledby="contact-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="contact-heading">Account &amp; contact</h2></div>
                        <div class="card-content">
                            <dl class="profile-data-grid">
                                <div><dt>Name</dt><dd>${provider.name()}</dd></div>
                                <div><dt>Email</dt><dd>${provider.email()}</dd></div>
                                <div><dt>Phone</dt><dd>${provider.phone()!''}</dd></div>
                            </dl>
                            <p class="profile-helper">Contact details are for account and appointment coordination. They should not be shown on a public profile unless explicitly approved for display.</p>
                            <a href="/providers/edit-details" class="btn profile-edit-btn">Edit contact details</a>
                        </div>
                    </section>

                    <section class="card provider-settings-card" aria-labelledby="professional-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="professional-heading">Public professional profile</h2></div>
                        <div class="card-content">
                            <dl class="profile-data-grid">
                                <#if provider?? && provider.accountStatus?? && provider.accountStatus().name() == "ACTIVE">
                                <div><dt>Service categories</dt><dd>
                                        <#if services??>
                                            <#list services as service>
                                                <#if service.status?? && service.status().name() == "APPROVED">
                                                    ${service.category().toString()}<br>
                                                </#if>
                                            </#list>
                                        </#if>
                                    <small class="text-muted">(from your approved services)</small></dd></div>
                                </#if>
                                <div><dt>Experience</dt><dd>${provider.yearsOfExperience()} years</dd></div>
                                <div class="profile-data-wide"><dt>About</dt><dd>${provider.bio()}</dd></div>
                                <div><dt>Address</dt><dd>${provider.addressLine()}, ${provider.city()}, ${provider.state()}, ${provider.zipcode()}</dd></div>
                                <div><dt>Certifications</dt><dd>${provider.certifications()}</dd></div>
                            </dl>
                            <p class="profile-helper">Certifications are self-reported and are not verified by GlowUp.</p>
                            <a href="/providers/edit-details" class="btn profile-edit-btn">Edit professional details</a>
                        </div>
                    </section>

                    <#if provider?? && provider.accountStatus?? && provider.accountStatus().name() == "ACTIVE">
                    <section class="card provider-settings-card" id="provider-services" aria-labelledby="services-heading">
                        <div class="profile-card-header provider-section-header">
                            <h2 class="profile-card-title" id="services-heading">Service listings</h2>
                            <a href="/providers/manage-services" class="btn btn-light">Manage services</a>
                        </div>
                        <div class="card-content">
                            <dl class="provider-availability-list mb-3">
                                    <#assign approvedCount = 0>
                                        <#if services??>
                                            <#list services as service>
                                                <#if service.status?? && service.status().name() == "APPROVED">
                                                    <#assign approvedCount = approvedCount + 1>
                                                </#if>
                                            </#list>
                                        </#if>
                                    <div><dt>Listings</dt><dd>${services?size} total · ${approvedCount} visible to customers</dd></div>
                                </dl>
                                <#if services??>
                                    <#list services as service>
                                        <#assign listingStatus = "is-pending">
                                            <#if service.isVisible()>
                                                <#assign listingStatus = "is-approved">
                                            </#if>
                                        <div class="provider-listing-row"><div><h3>${service.name()}</h3><p>${service.status()?lower_case?cap_first?replace("_", " ")}· ${service.durationMin()} min · $${service.price()?c}</p></div><span class="provider-listing-status ${listingStatus}"><#if service.isVisible()>Visible<#else>Hidden</#if></span></div>
                                    </#list>
                                </#if>
                                    <p class="profile-helper mb-0">Only approved listings appear in customer search and can be booked. Editing an approved listing submits a new version for review; the edited version stays hidden until approved.</p>
                                </dl>
                            </div>
                    </section>
                    </#if>

                    <section class="card provider-settings-card" id="provider-availability" aria-labelledby="availability-heading">
                        <div class="profile-card-header provider-section-header">
                            <h2 class="profile-card-title" id="availability-heading">Availability</h2>
                            <a href="/providers/manage-availability" class="btn btn-light">Manage availability</a>
                        </div>
                        <div class="card-content">
                            <dl class="provider-availability-list">
                            <#if providerSchedules??>
                                <#list providerSchedules as providerSchedule>
                                    <#if providerSchedule.isAvailable()>
                                        <div><dt>${providerSchedule.weekday()?lower_case?cap_first}</dt><dd>${providerSchedule.openTime()} – ${providerSchedule.closeTime()}</dd></div>
                                    <#else>
                                        <div><dt>${providerSchedule.weekday()?lower_case?cap_first}</dt><dd>Closed</dd></div> 
                                    </#if>
                                </#list>
                            </#if>
                            </dl>
                            <p class="profile-helper mb-0">Opening hours are not the same as a confirmed appointment. Changing your hours never cancels confirmed appointments.</p>
                        </div>
                    </section>

                    <section class="card provider-settings-card" aria-labelledby="account-settings-heading">
                        <div class="profile-card-header"><h2 class="profile-card-title" id="account-settings-heading">Account settings</h2></div>
                        <div class="card-content profile-settings-links">
                            <a href="security.html">Password &amp; sign-in security <span aria-hidden="true">→</span></a>
                            <a href="close-account.html">Close account <span aria-hidden="true">→</span></a>
                            <a href="../policies/privacy.html">Read the privacy policy <span aria-hidden="true">→</span></a>
                        </div>
                    </section>
                </div>
            </section>
        </main>

        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>