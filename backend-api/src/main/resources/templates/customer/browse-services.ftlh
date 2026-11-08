<#assign pageTitle = "Browse Services - GlowUp">
    <#include "/shared/head.ftlh">
        <#include "/shared/nav.ftlh">
        <main>
            <section class="section">
                <div class="container">
                    <div class="browse-services-header">
                        <h1 class="browse-services-title">Browse Our Services &amp; Providers</h1>
                        <div class="browse-services-subtitle"> Browse beauty professionals and find the service that's right for you</div>
                        <p class="small mt-2 mb-0">New provider services are reviewed before they appear here.</p>
                    </div>
                </div>
            </section>

            <section class="section">
                <div class="container">
                    <div class="filter-card">
                        <h3 class="filter-title">Filter services</h3>
                        <form method="get" action="/customers/browse-services" class="grid grid-4">
                            <div>
                                <label for="category-filter">Category</label>
                                <select id="category-filter" name="category">
                                    <option value="">All categories</option>
                                    <#list categoryOptions as option>
                                        <option value="${option.value}"<#if selectedCategory == option.value> selected</#if>>${option.label}</option>
                                    </#list>    
                                </select>
                            </div>
                            <div>
                                <label for="keyword-filter">Keyword</label>
                                <input type="text" id="keyword-filter" name="keyword" value="${keyword}" placeholder="Service or provider name">
                            </div>
                            <div>
                                <label for="city-filter">City</label>
                                <select id="city-filter" name="city">
                                    <option value="">Any city</option>
                                    <#list cities as cityName>
                                        <option value="${cityName}"<#if selectedCity == cityName> selected</#if>>${cityName}</option>
                                    </#list>
                                </select>
                            </div>
                            <div>
                                <label for="day-filter">Day</label>
                                <select id="day-filter" name="day">
                                    <option value="">Any day</option>
                                    <#list dayOptions as option>
                                        <option value="${option.value}"<#if selectedDay == option.value> selected</#if>>${option.label}</option>
                                    </#list>    
                                </select>
                            </div>
                            <div>
                                <label for="block-filter">Open during</label>
                                <select id="block-filter" name="timeOfDay">
                                    <option value="">Any time</option>
                                    <#list timeOptions as option>
                                        <option value="${option.value}"<#if selectedTime == option.value> selected</#if>>${option.label}</option>
                                    </#list>
                                </select>
                            </div>
                            <div>
                                <label for="rating-filter">Minimum rating</label>
                                <select id="rating-filter" name="minRating">
                                    <option value="">Any rating</option>
                                    <#list ratingOptions as option>
                                        <option value="${option.value}"<#if selectedRating == option.value> selected</#if>>${option.label}</option>
                                    </#list>    
                                </select>
                            </div>
                            <div>
                                <label for="sort-filter">Sort by</label>
                                <select id="sort-filter" name="sort">
                                    <#list sortOptions as option>
                                        <option value="${option.value}"<#if selectedSort == option.value> selected</#if>>${option.label}</option>
                                    </#list>
                                </select>
                            </div>
                            <div class="d-flex align-items-end">
                                <label class="d-flex align-items-center gap-2 mb-2">
                                    <input class="form-check-input" type="checkbox" name="savedOnly" value="true"<#if savedOnly> checked</#if>> Saved providers only
                                </label>
                            </div>
                            <div class="filter-btn-wrap">
                                <button type="submit" class="btn filter-apply-btn">Apply filters</button>
                                <a href="/customers/browse-services" class="btn profile-edit-btn">Clear</a>
                            </div>
                        </form>
                    </div>
                </div>
            </section>

            <section class="section">
                <div class="container">
                    <p class="profile-helper">${providers?size} provider(s) found. Saved providers are separate from your appointment history.</p>
                    <#if providers?size == 0>
                        <p class="profile-helper">No providers match your filters. Try removing one.</p>
                    </#if>
                    <div class="grid grid-3">
                        <#list providers as p>
                            <div class="provider-card">
                                <div class="provider-info">
                                    <h3 class="provider-name">${p.name()}</h3>
                                    <p class="provider-specialty"><#list p.categories() as category>${categoryLabels[category.name()]}<#sep>, </#list> &middot; ${p.city()}, ${p.state()}</p>
                                    <#if (p.bio())?has_content>
                                        <p class="provider-bio">${p.bio()?truncate(160)}</p>
                                    </#if>
                                    <#if (p.yearsOfExperience())??>
                                        <p class="provider-experience">${p.yearsOfExperience()} years of experience</p>
                                    </#if>
                                    <p class="provider-rating">
                                        <#if (p.averageRating())??>
                                            &#9733; ${p.averageRating()?string("0.0")} &middot; ${p.reviewCount()} review<#if p.reviewCount() != 1>s</#if>
                                        <#else>
                                            No reviews yet
                                        </#if>
                                    </p>
                                    <ul class="provider-services">
                                        <#list p.services() as service>
                                            <li>${service.name()} &middot; $${service.price()?string("0")} &middot; ${service.durationMin()} min</li>
                                        </#list>
                                    </ul>
                                    <div class="d-flex gap-2 provider-card-actions">
                                        <#if savedIds?seq_contains(p.providerId())>
                                            <form method="post" action="/customers/saved-providers/${p.providerId()?c}/remove">
                                                <input type="hidden" name="returnTo" value="${returnTo}">
                                                <button type="submit" class="btn btn-appt-secondary" aria-label="Remove ${p.name()} from saved providers">Saved &middot; Remove</button>
                                            </form>
                                        <#else>
                                            <form method="post" action="/customers/saved-providers/${p.providerId()?c}/save">
                                                <input type="hidden" name="returnTo" value="${returnTo}">
                                                <button type="submit" class="btn btn-appt-secondary" aria-label="Save ${p.name()}">Save</button>
                                            </form>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
        </section>
    </main>

        <footer><p class="mb-0">&copy; 2026 GlowUp. All rights reserved. Connecting customers with beauty professionals.</p></footer>
    </div>
</body>

</html>