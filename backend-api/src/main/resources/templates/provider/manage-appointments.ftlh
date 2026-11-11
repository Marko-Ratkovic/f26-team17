<#assign pageTitle="Manage Appointments - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
            <#if provider?? && provider.accountStatus?? && provider.accountStatus().name()=="PENDING_APPROVAL">
                <main>
                    <section class="section">
                        <div class="container">
                            <div class="provider-dashboard-hero">
                                <div>
                                    <p class="provider-eyebrow">PROVIDER WORKSPACE</p>
                                    <h1>Welcome back, ${provider.name()}!</h1>
                                    <p>Your account is pending review. Please wait for approval to view additional
                                        features. You can edit your profile
                                        <a href="/providers/profile" style="color:antiquewhite">here</a> or in My
                                        Profile tab.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </section>
                </main>
                <#else>
                    <main>
                        <section class="section">
                            <div class="container">
                                <div class="my-appointments-header">
                                    <h1 class="my-appointments-title">My Appointments</h1>
                                    <p class="my-appointments-subtitle">Review requests, upcoming bookings, and
                                        appointment history.</p>
                                </div>
                            </div>
                        </section>
                        <section class="section">
                            <div class="container">
                                <#assign requestAppt = 0>
                                <#assign confirmAppt = 0>
                                <#assign completeAppt = 0>
                                <#assign cancelAppt = 0>
                                <#assign declineAppt = 0>
                                <#if appointments??>
                                    <#list appointments as appt>
                                        <#if appt.status?? && appt.status().name()=="REQUESTED">
                                            <#assign requestAppt = requestAppt + 1>
                                        <#elseif appt.status?? && appt.status().name()=="CONFIRMED">
                                            <#assign confirmAppt = confirmAppt + 1>
                                        <#elseif appt.status?? && appt.status().name()=="COMPLETED">
                                            <#assign completeAppt = completeAppt + 1>
                                        <#elseif appt.status?? && appt.status().name()=="CANCELLED">
                                            <#assign cancelAppt = cancelAppt + 1> 
                                        <#elseif appt.status?? && appt.status().name()=="DECLINED">
                                            <#assign declineAppt = declineAppt + 1>        
                                        </#if>
                                    </#list>
                                </#if>
                                <nav class="appt-tabs provider-appointment-nav" aria-label="Jump to appointment status">
                                    <a href="#requests">Requests <span>(${requestAppt?c})</span></a>
                                    <a href="#confirmed">Upcoming <span>(${confirmAppt?c})</span></a>
                                    <a href="#completed">Completed <span>(${completeAppt?c})</span></a>
                                    <a href="#cancelled">Cancelled, Declined <span>(${cancelAppt + declineAppt})</span></a>
                                </nav>

                                <section class="appointment-list-section" id="requests"
                                    aria-labelledby="requests-heading">
                                    <h2 class="appointment-list-heading" id="requests-heading">Booking requests
                                        <span>(${requestAppt})</span></h2>
                                    <p class="profile-helper">A request is not a confirmed booking. Confirming reserves
                                        the time and emails the customer; declining requires a reason. For a reschedule,
                                        the original appointment stays confirmed unless you accept the new time.</p>
                                    <div class="table-responsive provider-table-wrap">
                                        <table class="table table-hover align-middle provider-appointment-table">
                                            <caption class="visually-hidden">Pending provider appointment requests
                                            </caption>
                                            <thead>
                                                <tr>
                                                    <th scope="col">Customer</th>
                                                    <th scope="col">Service</th>
                                                    <th scope="col">Requested time</th>
                                                    <th scope="col">Duration</th>
                                                    <th scope="col">Price</th>
                                                    <th scope="col">Status</th>
                                                    <th scope="col">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <#if appointments??>
                                                <#list appointments as appt>
                                                    <#if appt.status?? && appt.status().name()=="REQUESTED">
                                                        <tr>
                                                            <th scope="row">${appt.customerName()}</th>
                                                            <td>${appt.serviceName()}</td>
                                                            <td>${appt.formattedDate()}</td>
                                                            <td>${appt.durationMin()?c} min</td>
                                                            <td>$${appt.price()?c}</td>
                                                            <td><span class="provider-listing-status is-pending">Pending</span>
                                                            </td>
                                                            <td>
                                                                <div class="appt-actions d-flex">
                                                                    <form method="post" action="/providers/appointment/${appt.id()?c}/confirm">
                                                                        <button class="btn btn-sm btn-success" type="submit">Confirm</button>
                                                                    </form>
                                                                    <details class="report-details appt-reason-details mt-0">
                                                                        <summary class="btn" id="declineBtn">Decline</summary>
                                                                        <form method="post" action="/providers/appointment/${appt.id()?c}/decline">
                                                                            <label for="decline-alice">Reason for the customer (required)</label>
                                                                            <textarea id="decline-alice" name="reason" rows="2" maxlength="500" required
                                                                                placeholder="e.g. Fully booked that morning; any weekday afternoon works.">
                                                                            </textarea>
                                                                            <button type="submit" class="btn btn-sm btn-outline-danger">Decline request</button>
                                                                            <a class="btn btn-sm btn-outline-secondary" id="cancelDecline">Cancel</a>
                                                                        </form>
                                                                    </details>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </tbody>
                                        </table>
                                    </div>
                                </section>

                                <section class="appointment-list-section" id="confirmed"
                                    aria-labelledby="confirmed-heading">
                                    <h2 class="appointment-list-heading" id="confirmed-heading">Upcoming confirmed
                                        appointments <span>(${confirmAppt})</span></h2>
                                    <div class="table-responsive provider-table-wrap">
                                        <table class="table table-hover align-middle provider-appointment-table">
                                            <caption class="visually-hidden">Confirmed upcoming provider appointments
                                            </caption>
                                            <thead>
                                                <tr>
                                                    <th scope="col">Customer</th>
                                                    <th scope="col">Service</th>
                                                    <th scope="col">Date &amp; time</th>
                                                    <th scope="col">Duration</th>
                                                    <th scope="col">Price</th>
                                                    <th scope="col">Status</th>
                                                    <th scope="col">Details</th>
                                                    <th scope="col">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <#if appointments??>
                                                <#list appointments as appt>
                                                    <#if appt.status?? && appt.status().name()=="CONFIRMED">
                                                        <tr>
                                                            <th scope="row">${appt.customerName()}</th>
                                                            <td>${appt.serviceName()}</td>
                                                            <td>${appt.formattedDate()}</td>
                                                            <td>${appt.durationMin()?c} min</td>
                                                            <td>$${appt.price()?c}</td>
                                                            <td><span
                                                                    class="provider-listing-status is-approved">Confirmed</span>
                                                            </td>
                                                            <td><a href="">Appointment details</a></td>
                                                            <td>
                                                                <details class="report-details appt-reason-details">
                                                                    <summary class="btn">Cancel</summary>
                                                                    <form method="post" action="/providers/appointment/${appt.id()?c}/cancel">
                                                                        <label for="cancel-laura">Reason for the customer (required)</label>
                                                                        <textarea id="cancel-laura" name="reason" rows="2"
                                                                            maxlength="500" required></textarea>
                                                                        <p class="report-note">Only before the appointment
                                                                            starts. The customer is emailed.</p>
                                                                        <button type="submit" class="btn btn-sm btn-outline-danger">Cancel appointment</button>
                                                                        <a class="btn btn-sm btn-outline-secondary" id="cancelDecline">Cancel</a>
                                                                    </form>
                                                                </details>
                                                            </td>
                                                        </tr>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </tbody>
                                        </table>
                                    </div>
                                    <p class="profile-helper">Only access customer contact or private safety notes
                                        through that customer's confirmed appointment. Do not copy sensitive notes into
                                        general appointment lists.</p>
                                </section>

                                <section class="appointment-list-section" id="completed"
                                    aria-labelledby="completed-heading">
                                    <h2 class="appointment-list-heading" id="completed-heading">Completed appointments
                                        <span>(${completeAppt})</span></h2>
                                    <p class="profile-helper">A confirmed appointment counts as completed once its end
                                        time passes, so customers can review it even if you forget to mark it. You can
                                        still mark it completed yourself after it ends.</p>
                                    <div class="table-responsive provider-table-wrap">
                                        <table class="table table-hover align-middle provider-appointment-table">
                                            <caption class="visually-hidden">Completed provider appointments this month
                                            </caption>
                                            <thead>
                                                <tr>
                                                    <th scope="col">Customer</th>
                                                    <th scope="col">Service</th>
                                                    <th scope="col">Date</th>
                                                    <th scope="col">Duration</th>
                                                    <th scope="col">Price</th>
                                                    <th scope="col">Status</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <#if appointments??>
                                                <#list appointments as appt>
                                                    <#if appt.status?? && appt.status().name()=="COMPLETED">
                                                        <tr>
                                                            <th scope="row">${appt.customerName()}</th>
                                                            <td>${appt.serviceName()}</td>
                                                            <td>${appt.formattedDate()}</td>
                                                            <td>${appt.durationMin()?c} min</td>
                                                            <td>$${appt.price()?c}</td>
                                                            <td><span
                                                                    class="provider-listing-status is-approved">Completed</span>
                                                            </td>
                                                        </tr>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </tbody>
                                        </table>
                                    </div>
                                </section>

                                <section class="appointment-list-section" id="cancelled"
                                    aria-labelledby="cancelled-heading">
                                    <h2 class="appointment-list-heading" id="cancelled-heading">Cancelled and declined appointments
                                        <span>(${cancelAppt + declineAppt})</span></h2>
                                    <div class="table-responsive provider-table-wrap">
                                        <table class="table table-hover align-middle provider-appointment-table">
                                            <caption class="visually-hidden">Cancelled provider appointments this month
                                            </caption>
                                            <thead>
                                                <tr>
                                                    <th scope="col">Customer</th>
                                                    <th scope="col">Service</th>
                                                    <th scope="col">Original time</th>
                                                    <th scope="col">Cancelled by</th>
                                                    <th scope="col">Reason</th>
                                                    <th scope="col">Status</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <#if appointments??>
                                                <#list appointments as appt>
                                                    <#if appt.status?? && (appt.status().name()=="CANCELLED" || appt.status().name()=="DECLINED")>
                                                        <tr>
                                                        <th scope="row">${appt.customerName()}</th>
                                                        <td>${appt.serviceName()}</td>
                                                        <td>${appt.formattedDate()}</td>
                                                        <td>${(appt.cancelledBy()!)?lower_case?cap_first}</td>
                                                        <td>${appt.statusReason()}</td>
                                                        <td><span
                                                                class="provider-listing-status is-rejected">${appt.status()?lower_case?cap_first}</span>
                                                        </td>
                                                    </tr>
                                                    </#if>
                                                </#list>
                                            </#if>
                                            </tbody>
                                        </table>
                                    </div>
                                    <p class="profile-helper">Keep cancelled records for the booking history; do not
                                        delete them as a substitute for changing status.</p>
                                </section>
                            </div>
                        </section>
                    </main>
            </#if>
                    <footer>
                        <p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty
                            professionals.</p>
                    </footer>
                    </div>
                    </body>
                    <script>
                        const declineBtn = document.getElementById('declineBtn');
                        const cancelBtn = document.getElementById('cancelDecline');
                        cancelBtn.addEventListener('click', function() {
                            declineBtn.click();
                        });
                    </script>
                    </html>
            