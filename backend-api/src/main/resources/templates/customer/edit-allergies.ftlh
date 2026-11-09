<#assign pageTitle = "Browse Services - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="edit-info-header">
                        <h1 class="edit-info-title">Beauty &amp; safety notes</h1>
                        <p class="edit-info-subtitle">Record relevant allergies or sensitivities for your appointments.</p>
                    </div>
                </div>
            </section>
            <section class="section">
                <div class="container" style="max-width: 700px;">
                    <div class="card profile-card">
                        <div class="profile-card-header"><h2 class="profile-card-title">Private appointment information</h2></div>
                        <div class="card-content">
                            <p class="profile-privacy-callout"><strong>Who can see this?</strong> These notes are not shown on your public profile. The intended backend rule is to share them with the provider only for a confirmed appointment.</p>
                            <#if error??>
                                <div class="form-error" role="alert">${error}</div>
                            </#if>
                            <form method="post" action="/customers/edit-allergies">
                                <fieldset class="form-group">
                                    <legend class="profile-field-legend">Allergy information</legend>
                                    <label class="profile-choice"><input type="radio" name="allergyStatus" value="KNOWN"<#if formStatus == "KNOWN"> checked</#if>> I have allergies to share</label>
                                    <label class="profile-choice"><input type="radio" name="allergyStatus" value="NO_KNOWN"<#if formStatus == "NO_KNOWN"> checked</#if>> No known allergies</label>
                                    <label class="profile-choice"><input type="radio" name="allergyStatus" value="NOT_PROVIDED"<#if formStatus == "NOT_PROVIDED"> checked</#if>> I prefer not to provide this information</label>
                                    <label for="allergies">Allergy details <span class="optional-label">(required if you have allergies to share)</span></label>
                                    <textarea id="allergies" name="allergies" rows="3" maxlength="500">${formDetails}</textarea>
                                </fieldset>
                                <div class="form-group">
                                    <label for="sensitivities">Sensitivities or service-related notes <span class="optional-label">(optional)</span></label>
                                    <textarea id="sensitivities" name="sensitivities" rows="3" maxlength="500">${formSensitivities}</textarea>
                                    <small class="form-help">Share only information relevant to a beauty service. GlowUp is not a medical service; contact a qualified health professional about medical concerns.</small>
                                </div>
                                <div class="d-flex flex-wrap gap-2">
                                    <button type="submit" class="btn settings-btn settings-btn-sm">Save notes</button>
                                    <a href="/customers/profile" class="btn btn-appt-secondary">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        
        <footer><p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>