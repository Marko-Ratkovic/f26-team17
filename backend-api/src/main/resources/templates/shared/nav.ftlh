<nav class="sticky-top">
    <div class="navbar">
        <a href="/" class="navbar-brand mb-2">
            <img src="/images/logo.png" alt="" class="img-fluid logo" style="width:150; height: 50;">
        </a>
        <ul class="navbar-nav">
            <#if userRole?? && userRole=="provider">
                <li class="nav-item">
                    <a href="/providers/dashboard" class="nav-link under-scored">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a href="" class="nav-link under-scored">My Services</a>
                </li>
                <li class="nav-item">
                    <a href="" class="nav-link under-scored">My Appointments</a>
                </li>
                <li class="nav-item">
                    <a href="/providers/profile" class="nav-link under-scored">My Profile</a>
                </li>
                <#elseif userRole?? && userRole=="customer">
                <li class="nav-item">
                    <a href="#" class="nav-link under-scored">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a href="/customers/browse-services" class="nav-link under-scored">Browse Services &amp; Providers</a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link under-scored">My Appointments</a>
                </li>
                <li class="nav-item">
                    <a href="/customers/profile" class="nav-link under-scored">My Profile</a>
                </li>
                <#else>
                    <li class="nav-item">
                        <a href="/#features" class="nav-link under-scored">Features</a>
                    </li>
                    <li class="nav-item">
                        <a href="/#how-it-works" class="nav-link under-scored">How It Works</a>
                    </li>
                    <li class="nav-item">
                        <a href="/login" class="nav-link fw-bold pill-badge">Log in</a>
                    </li>
                    <li class="nav-item">
                        <a href="/register" class="nav-link fw-bold pill-badge">Get started</a>
                    </li>
            </#if>
            <#if userRole??>
                <li class="nav-item">
                    <form action="/logout" method="post">
                        <button type="submmit" class="nav-link fw-bold pill-badge">Logout</button>
                    </form>
                </li>
            </#if>
        </ul>
    </div>
</nav>