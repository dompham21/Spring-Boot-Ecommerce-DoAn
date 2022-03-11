
$('.dropdown-profile').click(function(){
    $('.dropdown-menu-profile').toggleClass('show');
    $('.dropdown-filter').toggleClass('hide-opacity');
});

$('.dropdown-filter').click(function(){

    $('.dropdown-menu-filter').toggleClass('show');

});

function showDeleteConfirmModal(link, entityName) {
    entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this "
        + entityName + " ID " + entityId + "?");
    $("#confirmModal").modal();
}


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
    let data = new FormData();
    data.append("file",event.target.files[0]);
    $.ajax ({
        data: data,
        type: "POST",
        url: "/file/upload",
        cache: false,
        enctype : 'multipart/form-data',
        contentType: false,
        processData: false,
        success: function(url) {
            let output = document.getElementById('image-output');
            $('#image-output').css('display', 'block')
            output.src = url;
            $('.upload-zone-content').css('display', 'none');
        },
        error: function(data) {
            console.log(data);
        }
    });

};

function uploadImage(image) {
    let data = new FormData();
    data.append("file",image);
    $.ajax ({
        data: data,
        type: "POST",
        url: "/file/upload",
        cache: false,
        enctype : 'multipart/form-data',
        contentType: false,
        processData: false,
        success: function(url) {
            $('#description').summernote("insertImage", url);
        },
        error: function(data) {
            console.log(data);
        }
    });
}

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

function handleInputNumberCart(evt, productId) {
    handleInputNumber(evt);
    updatedQuantityCart(productId, evt.target.value)
}

function showErrorLoginPage() {
    const wrapper = document.createElement('div');
    wrapper.innerHTML = "<div><div>You must login to add this product to cart.</div><div>Go to <a style='color: #4c78dd; font-weight: 700' href='/login'>Login Page</a></div></div>"
    swal( {
        title: '',
        icon: 'error',
        content: wrapper,
    })
}

$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});

function increaseQuantity(e, productId) {
    e.parentNode.querySelector('input[type=number]').stepUp();
    let quantity = $(e).parent().find('input').val()
    updatedQuantityCart(productId, quantity);

}

function decreaseQuantity(e, productId) {
    e.parentNode.querySelector('input[type=number]').stepDown();
    let quantity = $(e).parent().find('input').val()
    updatedQuantityCart(productId, quantity);
}

function updatedQuantityCart(productId, quantity) {
    $('.quantity' + productId).val(quantity);
    $('.price-quantity' +productId).text('đ x ' + quantity);
    url =  "/cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
    }).done(function(response) {
        if(response.status === "OK") {
            $(".subtotal" + productId).text(formatPrice(response.subtotal));
            $(".cart-estimatedTotal").text(formatPrice(response.estimatedTotal));
        }
    }).fail(function(error) {
        swal(
            '',
            error.responseText,
            'error'
        )
    });
}

function deleteItemCart(productId) {
    url = "/cart/delete/" + productId;
    $.ajax({
        type: "DELETE",
        url: url,
    }).done(function(response) {
        if(response.status === "OK") {
            $(".cart-estimatedTotal").text(formatPrice(response.estimatedTotal));
            removeItemFromHtml(productId);
            if($('.cart-item-count').length === 0) {
                $("#sectionEmptyCartMessage").removeClass("d-none");
                $("#sectionEmptyCartMessage").addClass("d-flex");
                $("#checkout-empty").removeClass("d-none");
                $("#checkout-empty").addClass("d-flex");

                $("#checkout-main").remove();
                $(".pulse-ring").remove();
                $(".nav-badge").remove();
            }
        }
    }).fail(function(error) {
        swal(
            '',
            error.responseText,
            'error'
        )
    });
}

function removeItemFromHtml(productId) {
    $(".cartitem"+productId).remove();
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
        swal(
            '',
            'Error loading district for the selected provice.',
            'error'
        )
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
        swal(
            '',
            'Error loading ward for the selected district.',
            'error'
        )
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
        swal(
            '',
            'Error loading district for the selected provice.',
            'error'
        )
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
        swal(
            '',
            'Error loading ward for the selected district.',
            'error'
        )
    })
}

function handleDetailLink(linkClass, modalId) {
    $(linkClass).on("click", function(e) {
        e.preventDefault();
        linkDetailURL = $(this).attr("href");
        $(modalId).modal("show").find(".modal-content").load(linkDetailURL);
    });
}