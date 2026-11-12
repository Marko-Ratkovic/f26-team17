<#assign pageTitle="Provider Dashboard - GlowUp">
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
                        <div class="provider-dashboard-hero">
                            <div>
                                <p class="provider-eyebrow">PROVIDER WORKSPACE</p>
                                <h1>Welcome back, ${provider.name()}!</h1>
                                <p>Here are your booking requests, upcoming appointments, and service review status.</p>
                            </div>
                            <a href="/providers/new-service" class="btn btn-light fw-bold">Add a service</a>
                        </div>
                        <div class="row g-3 provider-summary-grid">
                            <#assign requestAppt = 0>
                                <#assign confirmAppt = 0>
                                <#assign completeAppt = 0>
                                <#if appointments??>
                                    <#list appointments as appt>
                                        <#if appt.status?? && appt.status().name()=="REQUESTED">
                                            <#assign requestAppt = requestAppt + 1>
                                        <#elseif appt.status?? && appt.status().name()=="CONFIRMED">
                                            <#assign confirmAppt = confirmAppt + 1>
                                        <#elseif appt.status?? && appt.status().name()=="COMPLETED">
                                            <#assign completeAppt = completeAppt + 1> 
                                        </#if>
                                    </#list>
                                </#if>
                            <div class="col-md-6 col-xl-3"><a class="provider-summary-card"
                                    href="/providers/manage-appointments#requests"><span>Requests to
                                        review</span><strong>${requestAppt}</strong><small>Respond to booking requests</small></a>
                            </div>
                            <div class="col-md-6 col-xl-3">
                                <a class="provider-summary-card" href="/providers/manage-appointments#confirmed"><span>Upcoming bookings</span><strong>${confirmAppt}</strong><small>Confirmed appointments</small></a>
                            </div>
                            <div class="col-md-6 col-xl-3"><a class="provider-summary-card"
                                    href="/providers/manage-appointments#completed"><span>Completed appointments</span><strong>${completeAppt}</strong><small>Appointment history</small></a></div>
                            <div class="col-md-6 col-xl-3"><a class="provider-summary-card"
                                    href="#recent-reviews"><span>Customer rating</span><strong>4.8 <small>/
                                            5</small></strong><small>Across 10 reviews</small></a></div>
                        </div>
                    </div>
                </section>

                <section class="section">
                    <div class="container">
                        <div class="provider-content-heading">
                            <div>
                                <h2>Upcomming appointments</h2>
                                <p>Confirmed bookings only. Pending requests are listed separately.</p>
                            </div><a href="/providers/manage-appointments"
                                class="btn btn-appt-secondary align-self-center">View all appointments</a>
                        </div>
                        <div class="provider-dashboard-list">
                            <#if appointments??>
                                <#list appointments as appt>
                                    <#if appt.status?? && appt.status().name()=="CONFIRMED">
                                        <article class="provider-dashboard-booking">
                                            <div class="d-flex justify-content-between">
                                                <div class="fw-bold align-content-center">
                                                    <h3>${appt.formattedDate()}</h3>
                                                </div>
                                                <span class="provider-listing-status is-approved">Confirmed</span>
                                            </div>
                                            <div class="d-flex justify-content-between align-content-center mt-2">
                                                <div class="mt-1 mb-2">
                                                    <div class="fs-5">${appt.serviceName()} - <span
                                                            class="provider-booking-price">$${appt.price()?c} <small>· ${appt.durationMin()?c} min</small></span></div>
                                                    <p>${appt.customerName()}</p>
                                                </div>
                                                <div class="align-self-center">
                                                    <a href="/providers/appointment/${appt.id()?c}/details" class="btn btn-appt-secondary">View details</a>
                                                </div>
                                            </div>
                                        </article>
                                    </#if>
                                </#list>
                            </#if>
                        </div>
                    </div>
                </section>

                <section class="section">
                    <div class="container">
                        <div class="provider-content-heading">
                            <div>
                                <h2>Service listing status</h2>
                                <p>Only approved listings are visible and bookable.</p>
                            </div>
                            <a href="/providers/manage-services" class="btn btn-appt-secondary align-self-center">Manage
                                services</a>
                        </div>
                        <div class="provider-status-summary">
                            <#assign approvedCount = 0>
                            <#assign pendingCount = 0>
                            <#assign changesRequestCount = 0>
                                <#if services??>
                                    <#list services as service>
                                        <#if service.status?? && service.status().name() == "APPROVED">
                                            <#assign approvedCount = approvedCount + 1>
                                        <#elseif service.status?? && service.status().name() == "PENDING">
                                            <#assign pendingCount = pendingCount + 1>
                                        <#elseif service.status?? && service.status().name() == "CHANGES_REQUESTED">
                                            <#assign changesRequestCount = changesRequestCount + 1>
                                        </#if>
                                    </#list>
                                </#if>
                            <div><span class="provider-listing-status is-approved">Approved</span><strong>${approvedCount}</strong>
                                <p>Visible to customers</p>
                            </div>
                            <div><span class="provider-listing-status is-pending">Pending
                                    review</span><strong>${pendingCount}</strong>
                                <p>Hidden while awaiting review</p>
                            </div>
                            <div><span class="provider-listing-status is-pending">Changes
                                    requested</span><strong>${changesRequestCount}</strong>
                                <p>Hidden; update and resubmit</p>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="section" id="recent-reviews">
                    <div class="container">
                        <div class="provider-content-heading">
                            <div>
                                <h2>Reviews needing a reply</h2>
                                <p>You can reply once to each review. Replies cannot be edited after posting.</p>
                            </div><a href="reviews.html" class="btn btn-appt-secondary align-self-center">View all
                                reviews</a>
                        </div>
                        <article class="provider-review-card">
                            <div class="provider-review-heading">
                                <div>
                                    <h3>Bill T.</h3>
                                    <p>Nail design · 5/5 ⭐ · Completed appointment</p>
                                </div><time datetime="2026-10-06">Oct 6, 2026</time>
                            </div>
                            <blockquote>“It was an awesome experience. Michelle was kind and made sure I was
                                comfortable.”</blockquote>
                            <details class="report-details">
                                <summary>Report review</summary>
                                <form method="post">
                                    <label for="report-dash-bill-t-review-reason">Why are you reporting this
                                        review?</label>
                                    <select id="report-dash-bill-t-review-reason" name="reason" required>
                                        <option value="" selected disabled>Choose a reason</option>
                                        <option value="HARASSMENT">Harassment</option>
                                        <option value="THREATS">Threats</option>
                                        <option value="HATE_OR_EXPLICIT">Hate or explicit content</option>
                                        <option value="PERSONAL_INFORMATION">Personal information</option>
                                        <option value="SPAM">Spam</option>
                                        <option value="OFF_TOPIC">Off-topic content</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                    <label for="report-dash-bill-t-review-details">Details <span
                                            class="report-optional">(required for Other)</span></label>
                                    <textarea id="report-dash-bill-t-review-details" name="details" rows="2"
                                        maxlength="500" placeholder="What breaks the community guidelines?"></textarea>
                                    <p class="report-note">Low ratings and honest criticism are not violations. The
                                        review stays visible while an admin checks it, and the author is not told who
                                        reported it.</p>
                                    <button type="submit" class="btn btn-appt-primary btn-sm" disabled>Submit
                                        report</button>
                                </form>
                            </details>
                            <details class="provider-reply-details">
                                <summary>Reply to this review</summary>
                                <form method="post">
                                    <label for="reply-bill">Your public reply</label>
                                    <textarea id="reply-bill" name="reply" rows="3" maxlength="1000" required
                                        placeholder="Write a respectful reply about the service."></textarea>
                                    <p class="profile-helper">Replies are public. You can reply once and cannot edit it
                                        later. Do not include private customer details.</p>
                                    <button type="submit" class="btn btn-appt-primary" disabled>Submit reply</button>
                                </form>
                            </details>
                        </article>
                        <article class="provider-review-card">
                            <div class="provider-review-heading">
                                <div>
                                    <h3>Alison B.</h3>
                                    <p>Pedicure · 4/5 ⭐ · Completed appointment</p>
                                </div><time datetime="2026-10-02">Oct 2, 2026</time>
                            </div>
                            <blockquote>“Great service! Clean and friendly. I had a relaxing visit.”</blockquote>
                            <details class="report-details">
                                <summary>Report review</summary>
                                <form method="post">
                                    <label for="report-dash-alison-b-review-reason">Why are you reporting this
                                        review?</label>
                                    <select id="report-dash-alison-b-review-reason" name="reason" required>
                                        <option value="" selected disabled>Choose a reason</option>
                                        <option value="HARASSMENT">Harassment</option>
                                        <option value="THREATS">Threats</option>
                                        <option value="HATE_OR_EXPLICIT">Hate or explicit content</option>
                                        <option value="PERSONAL_INFORMATION">Personal information</option>
                                        <option value="SPAM">Spam</option>
                                        <option value="OFF_TOPIC">Off-topic content</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                    <label for="report-dash-alison-b-review-details">Details <span
                                            class="report-optional">(required for Other)</span></label>
                                    <textarea id="report-dash-alison-b-review-details" name="details" rows="2"
                                        maxlength="500" placeholder="What breaks the community guidelines?"></textarea>
                                    <p class="report-note">Low ratings and honest criticism are not violations. The
                                        review stays visible while an admin checks it, and the author is not told who
                                        reported it.</p>
                                    <button type="submit" class="btn btn-appt-primary btn-sm" disabled>Submit
                                        report</button>
                                </form>
                            </details>
                            <details class="provider-reply-details">
                                <summary>Reply to this review</summary>
                                <form method="post">
                                    <label for="reply-alison">Your public reply</label>
                                    <textarea id="reply-alison" name="reply" rows="3" maxlength="1000" required
                                        placeholder="Write a respectful reply about the service."></textarea>
                                    <p class="profile-helper">Replies are public. You can reply once and cannot edit it
                                        later. Do not include private customer details.</p>
                                    <button type="submit" class="btn btn-appt-primary" disabled>Submit reply</button>
                                </form>
                            </details>
                        </article>
                    </div>
                </section>
            </main>
        </#if>
            
            <footer>
                <p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p>
            </footer>
            </div>
            </body>

            </html>