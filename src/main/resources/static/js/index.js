
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
    return parseFloat(inputPrice).toLocaleString('de-DE', {minimumFractionDigits: 0, maximumFractionDigits: 0});
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

