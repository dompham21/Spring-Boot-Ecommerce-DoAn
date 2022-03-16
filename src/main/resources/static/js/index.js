
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
    return parseFloat(inputPrice).toLocaleString('en-US', {minimumFractionDigits: 0, maximumFractionDigits: 0});
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
    let reader = new FileReader();
    reader.onload = function(){
        let output = document.getElementById('image-output');
        output.src = reader.result;
        $('#image-output').css('display', 'block')
        $('#image').val(reader.result)
        $('.upload-zone-content').css('display', 'none');
    };
    reader.readAsDataURL(event.target.files[0]);
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
    wrapper.innerHTML = "<div><div>You must login to add this product to cart.</div><div>Go to <a style='color: #4c78dd; font-weight: 700' href='/login'>Login Page</a></div></div>"

    Swal.fire({
        title: '',
        content: wrapper,
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
        title: "Are you sure you want to delete this "
            + entityName + " ID " + entityId + "?",
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: `<a href= ${url}>Yes, delete it!</a>`,
    })
}

function btnDeleteCartItem(event,productId) {
    event.preventDefault();
    let url = '/cart/delete/' + productId
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: `<a href= ${url}>Yes, delete it!</a>`,
    })
}


function loadDistrictByProvinces() {
    let selectedProvice = $("#province option:selected");
    let provinceCode = selectedProvice.val();
    let dropdownDistrict = $("#district");
    let url = "/api/district/" + provinceCode;

    $.get(url, function(responseJson) {
        dropdownDistrict.empty();
        $("<option>").val("").text("Chọn quận/huyện").attr("disabled", "disabled").attr("selected","selected").appendTo(dropdownDistrict);
        $.each(responseJson, function(index, district) {
            $("<option>").val(district.code).text(district.name).appendTo(dropdownDistrict);
        });

    }).fail(function() {
        Swal.fire({
            title: '',
            text: 'Error loading district for the selected provice.',
            icon: 'error'
        })

    })
}



function loadWardByDistricts() {
    let selectedDistrict = $("#district option:selected");
    let districtCode = selectedDistrict.val();
    let dropdownWard = $("#ward");
    let url = "/api/ward/" + districtCode;

    $.get(url, function(responseJson) {
        dropdownWard.empty();
        $("<option>").val("").text("Chọn phường/xã").attr("disabled", "disabled").attr("selected","selected").appendTo(dropdownWard);
        $.each(responseJson, function(index, ward) {
            $("<option>").val(ward.code).text(ward.name).appendTo(dropdownWard);
        });

    }).fail(function() {
        Swal.fire({
            title: '',
            text: 'Error loading ward for the selected district.',
            icon: 'error'
        })

    })
}


function loadDistrictByProvincesEdit() {
    let selectedProvice = $("#province option:selected");
    let provinceCode = selectedProvice.val();
    let dropdownDistrict = $("#district");
    let url = "/api/district/" + provinceCode;

    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, district) {
            $("<option>").val(district.code).text(district.name).appendTo(dropdownDistrict);
        });

        $("#district option").each(function() { //remove option duplicate when form is edit
            $(this).siblings('[value="'+ this.value +'"]').remove();
        });

    }).fail(function() {
        Swal.fire({
            title: '',
            text: 'Error loading district for the selected provice',
            icon: 'error'
        })

    })
}

function loadWardByDistrictsEdit() {
    let selectedDistrict = $("#district option:selected");
    let districtCode = selectedDistrict.val();
    let dropdownWard = $("#ward");
    let url = "/api/ward/" + districtCode;

    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, ward) {
            $("<option>").val(ward.code).text(ward.name).appendTo(dropdownWard);
        });
        $("#ward option").each(function() { //remove option duplicate when form is edit
            $(this).siblings('[value="'+ this.value +'"]').remove();
        });

    }).fail(function() {
        Swal.fire({
            title: '',
            text: 'Error loading ward for the selected district.',
            icon: 'error'
        })

    })
}

function handleDetailLink(linkClass, modalId) {
    $(linkClass).on("click", function(e) {
        e.preventDefault();
        linkDetailURL = $(this).attr("href");
        $(modalId).modal("show").find(".modal-content").load(linkDetailURL);
    });
}