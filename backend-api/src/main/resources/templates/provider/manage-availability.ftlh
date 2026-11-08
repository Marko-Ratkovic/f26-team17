<#assign pageTitle="Provider Profile - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section"><div class="container"><div class="edit-info-header">
                <h1 class="edit-info-title">Manage availability</h1>
                <p class="edit-info-subtitle">Set the days you work and your opening hours for each day.</p>
            </div></div></section>
            <section class="section">
                <div class="container">
                    <div class="card profile-card">
                        <div class="profile-card-header"><h2 class="profile-card-title">Weekly schedule</h2></div>
                        <div class="card-content">
                            <form method="post" action="">
                                <div class="table-responsive">
                                    <table class="table provider-schedule-table">
                                        <caption class="visually-hidden">Weekly opening hours</caption>
                                        <thead><tr>
                                            <th scope="col">Day</th>
                                            <th scope="col">Open</th>
                                            <th scope="col">Opens at</th>
                                            <th scope="col">Closes at</th>
                                        </tr></thead>
                                        <tbody>
                                         <#list providerSchedules as providerSchedule>
                                            <tr><th scope="row">${providerSchedule.weekday()?lower_case?cap_first}</th>
                                            <td>
                                                <input class="form-check-input" type="checkbox" id="${providerSchedule.weekday()?lower_case}-open" name="isAvailable" value="${providerSchedule.isAvailable()?string('true','false')}" <#if providerSchedule.isAvailable()>checked</#if>>
                                                <label class="visually-hidden" for="${providerSchedule.weekday()?lower_case}-open">Open on ${providerSchedule.weekday()?lower_case?cap_first}</label>
                                            </td>
                                            <td>
                                                <label class="visually-hidden" for="${providerSchedule.weekday()?lower_case}-opens">${providerSchedule.weekday()?lower_case?cap_first} opening time</label>
                                                <input type="time" class="form-control form-control-sm schedule-time" id="${providerSchedule.weekday()?lower_case}-opens" name="openTime" value="${providerSchedule.openTime()}" <#if !providerSchedule.isAvailable()>disabled</#if> step="900">
                                            </td>
                                            <td>
                                                <label class="visually-hidden" for="${providerSchedule.weekday()?lower_case}-closes">${providerSchedule.weekday()?lower_case?cap_first} closing time</label>
                                                <input type="time" class="form-control form-control-sm schedule-time" id="${providerSchedule.weekday()?lower_case}-closes" name="closeTime" value="${providerSchedule.closeTime()}" <#if !providerSchedule.isAvailable()>disabled</#if> step="900">
                                            </td>
                                            </tr>
                                        </#list>   
                                        </tbody>
                                    </table>
                                </div>
                                <p class="profile-helper">Customers can only request times that fit inside your hours for that day. An unchecked day is closed, and its hours stay saved for when you reopen it. Changing your hours never cancels confirmed appointments.</p>
                                <div class="d-flex flex-wrap gap-2">
                                    <button type="submit" class="btn btn-appt-primary">Save schedule</button>
                                    <a href="profile.html#provider-availability" class="btn btn-appt-secondary">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
    <script>
        const weekDay= document.querySelectorAll('input[type="checkbox"].form-check-input');
        weekDay.forEach(checkbox => {
            checkbox.addEventListener('change', (event) => {
                const row = event.target.closest('tr');
                const timeInputs = row.querySelectorAll('input[type="time"].schedule-time');
                const isChecked = event.target.checked;
                if (isChecked) {
                    checkbox.value = "true";
                } else {
                    checkbox.value = "false";
                }
                timeInputs.forEach(input => {
                    input.disabled = !isChecked;
                });

            });
        });
    </script>
</body>

</html>