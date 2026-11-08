<#assign pageTitle="New Service - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <#if provider?? && provider.accountStatus?? && provider.accountStatus().name() == "ACTIVE">
            <section class="section">
                <div class="container" style="max-width: 700px;">
                    <div class="card rounded-4">
                        <div class="card-content">
                            <div class="card-title text-center align-items-center gap-2 mt-4">
                                <h1 class="h2">Add a new service</h1>
                                <p class="text-secondary sm ">Submit a service listing for review before it appears to customers.</p>

                            </div>
                            <form class="provider-form" method="post" action="" enctype="multipart/form-data">
                                <div class="alert alert-info" role="note">
                                    New listings stay hidden from customers until an admin approves them. Check your service
                                    details against the <a href="../policies/terms.html">GlowUp terms</a> before submitting.
                                </div>
                                <div class="mb-3">
                                    <label for="svImg" class="form-label fw-bold">Upload service image</label>
                                    <input class="form-control" type="file" id="svImg" name="image" accept="image/png,image/jpeg,image/webp" required>
                                    <small class="form-help">Use an image you have permission to publish. The live backend should enforce file type and size limits.</small>
                                </div>
                                <div class="mb-3">
                                    <label for="svName" class="form-label fw-bold">Service name</label>
                                    <input type="text" class="form-control" id="svName" name="serviceName"
                                        placeholder="Pedicure" maxlength="80" required>
                                </div>
                                <div class="mb-3">
                                    <label for="svCategory" class="form-label fw-bold">Service category</label>
                                    <select class="form-select" id="svCategory" name="serviceCategory" required>
                                        <option value="">Select a category</option>
                                        <option value="HAIR_STYLING">Hair Styling</option>
                                        <option value="NAIL_CARE">Nail Care</option>
                                        <option value="MAKEUP_ARTISTRY">Makeup Artistry</option>
                                        <option value="SKINCARE">Skincare</option>
                                        <option value="BROWS_AND_LASHES">Brows &amp; Lashes</option>
                                        <option value="HAIR_REMOVAL">Hair Removal &amp; Body Grooming</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="svDescript" class="form-label fw-bold">Description</label>
                                    <textarea class="form-control" id="svDescript" name="description" rows="3"
                                        maxlength="1000" placeholder="Describe exactly what customers receive..." required></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="svDuration" class="form-label fw-bold">Duration (minutes)</label>
                                    <input type="number" class="form-control" id="svDuration" name="durationMinutes"
                                        placeholder="30" min="5" max="480" required>
                                </div>
                                <div class="mb-3">
                                    <label for="svPrice" class="form-label fw-bold">Price (USD)</label>
                                    <input type="number" class="form-control" id="svPrice" name="price"
                                        placeholder="45" min="1" step="1" required>
                                    <small class="form-help">Whole dollars. Shown to customers; no payment is taken in the app.</small>
                                </div>
                                <div class="d-flex justify-content-end align-items-center gap-2 mt-4">
                                    <button type="submit" class="btn btn-dark" disabled>Submit for review</button>
                                    <a href="manage-services.html" class="btn btn-outline-secondary">Cancel</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </#if>

        <!--Footer-->
        <footer>
            <p class="mb-0">© 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.
            </p>
        </footer>
    </div>

</body>

</html>