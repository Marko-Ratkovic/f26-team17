<#assign pageTitle="Provider Services - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <#if provider?? && provider.accountStatus?? && provider.accountStatus().name() == "PENDING_APPROVAL">
            <main>
                <section class="section">
                    <div class="container">
                        <div class="provider-dashboard-hero">
                            <div>
                                <p class="provider-eyebrow">PROVIDER WORKSPACE</p>
                                <h1>Welcome back, ${provider.name()}!</h1>
                                <p>Your account is pending review. Please wait for approval to view additional features. You can edit your profile 
                                    <a href="/providers/profile" style="color:antiquewhite">here</a> or in My Profile tab.</p>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
        <#else>
            <main>
                <section class="section">
                    <div class="container">
                        <div class="provider-content-heading provider-services-heading d-flex align-items-center">
                            <div><h1>Manage services</h1><p>Review listing status and keep published details accurate.</p></div>
                            <a href="/providers/new-service" class="btn btn-appt-primary">Add a service</a>
                        </div>
                        <div class="profile-privacy-callout"><strong>Review rule:</strong> New listings and edited listing versions stay hidden until approved. A prior approved version is not silently changed by an unreviewed edit.</div>
                        <div class="row g-3 provider-service-grid">
                            <#list services as service>
                                <div class="col-lg-6">
                                    <article class="card provider-service-card h-100">
                                            <img src="/images/services/${(service.imagePath())!'service-default.jpg'}" class="provider-service-image" alt="${service.name()}">
                                            <div class="card-body">
                                                <div class="provider-service-title">
                                                    <div>
                                                        <h2>${service.name()}</h2>
                                                        <span class="provider-listing-status is-<#if service.status()=="APPROVED">approved<#else>pending</#if>">${service.status()?lower_case?cap_first} · <#if service.isVisible()>visible<#else>hidden</#if></span>
                                                    </div><p>$${service.price()?c}</p>
                                                </div>
                                                    <p>${service.description()}</p>
                                                        <#if service.status() == "CHANGES_REQUESTED" && !service.isVisible()>
                                                            <div class="provider-review-reason"><strong>Admin feedback</strong><p>${service.reviewNote()}</p></div>
                                                        </#if>
                                                        <dl class="provider-service-meta">
                                                            <div>
                                                                <dt>Category</dt><dd>${service.category()}</dd>
                                                            </div>
                                                            <div>
                                                                <dt>Duration</dt><dd>${service.durationMin()} minutes</dd>
                                                            </div>
                                                        
                                                            <#if service.status() == "APPROVED" && service.isVisible()>
                                                            </dl>
                                                            <div class="d-flex flex-wrap gap-2"><a href="edit-service.html" class="btn btn-appt-secondary">Edit listing</a><button class="btn btn-outline-danger" type="button" style="border-radius: var(--border-radius)">Archive</button></div>
                                                            <#elseif service.status() == "PENDING" && !service.isVisible()>    
                                                                <div>
                                                                    <dt>Submitted at</dt><dd>${service.updatedAt()}</dd>
                                                                </div>
                                                                </dl>
                                                                <button class="btn btn-outline-secondary" type="button" disabled>Awaiting admin review</button>
                                                            <#elseif service.status() == "CHANGES_REQUESTED" && !service.isVisible()>
                                                                </dl>
                                                                <a href="edit-service.html" class="btn btn-appt-primary">Revise and resubmit</a>
                                                            <#elseif service.status() == "ARCHIVED" && !service.isVisible()>
                                                                </dl>
                                                                <button class="btn btn-outline-danger" type="button" style="border-radius: var(--border-radius)" disabled>Archived</button>
                                                            </#if>
                                            </div>
                                    </article>
                                </div>
                            </#list>
                        </div>
                    </div>
                </section>
            </main>
        </#if>
        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>