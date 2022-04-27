
$('.dropdown-profile').click(function(){
    $('.dropdown-menu-profile').toggleClass('show');
    $('.dropdown-filter').toggleClass('hide-opacity');
});

$('.dropdown-filter').click(function(){

    $('.dropdown-menu-filter').toggleClass('show');

});


function formatNumber(event, input) {
    if(event.which >= 37 && event.which <= 40){
        event.preventDefault();
    }
    input.val(function(index, value) {
        return value
            .replace(/\D/g, "")
            .replace(/([0-9])([0-9]{3})$/, '$1.$2')
            .replace(/\B(?=(\d{3})+(?!\d)\.?)/g, ".")
            .replace(/^0+(\d)/, '$1')
            ;
    });
}

/** double to price */
function formatPrice(inputPrice) {
    return parseFloat(inputPrice).toLocaleString('en-US', {minimumFractionDigits: 0, maximumFractionDigits: 0}).replaceAll(",", ".");
}


/**  yyyy-mm-dd to dd/mm/yyyy   */
function formatDate(inputDate) {
    let date = new Date(inputDate);
    if (!isNaN(date.getTime())) {
        // Months use 0 index.
        const day = String(date.getDate()).padStart(2, '0');

        return day + '/' + date.getMonth() + 1 + '/' + date.getFullYear();
    }
}


let loadFile = function(event) {
    let output = document.getElementById('image-output');
    let url = URL.createObjectURL(event.target.files[0]);
    output.src = url;

    output.onload = function(){
        URL.revokeObjectURL(output.src) // free memory
        $('#image-output').css('display', 'block')
        $('#image').val(url);
        $('.upload-zone-content').css('display', 'none');
    };
};


function clearFilter(entityName) {
    window.location = `/admin/${entityName}/page/1`;
}

function handleInputNumber(evt) {
    let max = parseInt(evt.currentTarget.getAttribute('max'));
    let number = parseInt(evt.target.value);
    if(number >= max) {
        evt.target.value = max;
    }
    else if(number <=0 || isNaN(number)) {
        evt.target.value = 1;
    }
    else {
        evt.target.value = number;
    }
}



function showErrorLoginPage() {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = "<div><div>Bạn phải đăng nhập để thêm sản phẩm này vào giỏ hàng.</div><div>Đi đến <a style='color: #4c78dd; font-weight: 700' href='/login'>Trang Đăng Nhập</a></div></div>"

    Swal.fire({
        title: '',
        html: wrapper,
        icon: 'error'
    })

}

$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});



function showConfirmDelete(event, entityId ) {
    event.preventDefault();
    let link = $("#link-delete-" + entityId)
    let entityName = link.attr("entity")

    let url = link.attr("href")


    Swal.fire({
        title: "Bạn có chắc chắn muốn xóa "
            + entityName + " ID " + entityId + "?",
        text: "Bạn sẽ không thể hoàn tác điều này!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: `<a href=${url} style="padding: 0.625em 1.1em;">Yes, delete it!</a>`,
    })
}

function btnDeleteCartItem(event,productId) {
    event.preventDefault();
    let url = '/cart/delete/' + productId
    Swal.fire({
        title: 'Bạn có chắc chắn không?',
        text: "Bạn sẽ không thể hoàn tác điều này!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: `<a href=${url} style="padding: 0.625em 1.1em;">Yes, delete it!</a>`,
    })
}

function handleDetailLink(linkClass, modalId) {
    $(linkClass).on("click", function(e) {
        e.preventDefault();
        linkDetailURL = $(this).attr("href");
        $(modalId).modal("show").find(".modal-content").load(linkDetailURL);
    });
}