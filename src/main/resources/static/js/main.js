document.addEventListener('DOMContentLoaded', function ()
{
	const toggleButton = document.getElementById('sidebar-toggle');
	const sidebar = document.getElementById('sidebar');
	const headings = document.querySelectorAll('.sidebar-heading');
	const submenus = document.querySelectorAll('.sidebar-submenu');

	toggleButton.addEventListener('click', function ()
	{
		const opened = toggleButton.getAttribute('aria-expanded') === 'true';
		if (opened)
			closeSidebar();
		else
			openSidebar();
	});

	// Close when clicking a sidebar link on small screens
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

	function openSidebar()
	{
		console.log("Open sidebar");
		toggleButton.setAttribute('aria-expanded', 'true');
		sidebar.setAttribute('aria-hidden', 'false');
		sidebar.classList.remove('toggle');
	}

	function closeSidebar()
	{
		console.log("Close sidebar");
		toggleButton.setAttribute('aria-expanded', 'false');
		sidebar.setAttribute('aria-hidden', 'true');
		sidebar.classList.add('toggle');
	}

});

function toggleDialogWindow()
{
	const dialog = document.getElementById('dialog');
	dialog.classList.toggle('hide');
}