document.addEventListener('DOMContentLoaded', function ()
{
    const toggleButton = document.getElementById('sidebar-toggle');
	toggleButton.addEventListener('click', function ()
	{
		const opened = toggleButton.getAttribute('aria-expanded') === 'true';
		if (opened)
			closeSidebar();
		else
			openSidebar();
	});

    const sidebar = document.getElementById('sidebar');
	sidebar.addEventListener('click', function (e)
	{
		const link = e.target.closest('a');
		if (!link)
			return;
		if (window.matchMedia('(max-width: 768px)').matches)
		{
		 	closeSidebar();
		}
	});

    function openSidebar()
    {
    	toggleButton.setAttribute('aria-expanded', 'true');
    	sidebar.classList.remove('toggle');
    }

    function closeSidebar()
    {
    	toggleButton.setAttribute('aria-expanded', 'false');
    	sidebar.classList.add('toggle');
    }

    const headings = document.querySelectorAll('.sidebar-heading');
    const submenus = document.querySelectorAll('.sidebar-submenu');
    headings.forEach(heading =>
    {
        heading.addEventListener('click', function()
        {
            const submenu = this.nextElementSibling;
            if (submenu && submenu.classList.contains('sidebar-submenu')) {
                submenus.forEach(otherSubmenu => {
                    if (otherSubmenu !== submenu && otherSubmenu.classList.contains('sidebar-active')) {
                        otherSubmenu.classList.remove('sidebar-active');
                    }
                });
                submenu.classList.toggle('sidebar-active');
            }
        });
    });
});

function toggleDialogWindow()
{
    const dialog = document.getElementById('dialog');
	dialog.classList.toggle('hide');
}

async function sync(event, url)
{
    event.stopPropagation();
    const element = document.getElementById("syncButton");
    const originalText = element.textContent;
    element.disabled = true;
    element.textContent = '...';

    const response = await doGetRequest(url);

    if (response.ok)
    {
        element.innerHTML = '✅';
    }
    else
    {
        element.innerHTML = '❌';
    }

    element.disabled = false;
}

async function check(url, formId)
{
    const testButton = document.getElementById("testButton");
    const originalText = testButton.textContent;
    testButton.disabled = true;
    testButton.textContent = '...';

    const response = await doPostRequest(url, formId);

    if (response.ok)
    {
        testButton.textContent = '✅';
    }
    else
    {
        testButton.textContent = '❌';
    }

    testButton.disabled = false;
}

async function search(uri)
{
	const searchInput = document.getElementById("search");
	url = uri + "?search=" + searchInput.value;
    const response = await doGetRequest(url);
    document.getElementById("searchResults").innerHTML = response;
}

async function doGetRequest(url)
{
	const response = await fetch(url, {
		method: "GET"
	});
	return await response.text();
}

async function doPostRequest(url, formId)
{
	const formElement = document.getElementById(formId);
	const formData = new FormData(formElement);
	const response = await fetch(url, {
		method: "POST",
		body: formData,
	});
	return await response.text();
}