$(document).ready(function() {

    if ($(".userFilesList") != null) {
        updateFilesList();
    }

    function updateFilesList() {
        $.getJSON("/account/allFilesInformation", function(data) {
            var fileTable = d3.select(".userFilesList");
            var count = 0;
            fileTable.html("");
            data.forEach(function(elem) {
                var liMain = fileTable.append("li")
                    .attr("class", function() {
                        var cls = "dropdown";
                        if (elem["rendering"]) {
                            cls += " disabled";
                        }
                        if (elem["renderCount"] == 0) {
                            cls += " waitForRender";
                        }
                        if (!elem["rendered"]) {
                            cls += " error";
                        }
                        return cls;
                    });
                var a = liMain.append("a").style("cursor", "pointer");

                a.style("background-color", function() {
                        if (elem["rendering"] || !elem["rendered"]) {
                            return "#DCDCDC";
                        }
                        return "#FFFFFF";
                    }).on("mouseover", function() {
                        $(this).css("background-color", "#DCDCDC");
                        if (!liMain.classed("disabled") && !liMain.classed("waitForRender")) {
                            $(this).find(".fa-trash").css("visibility", "visible");
                        }
                    }).on("mouseout", function() {
                        if (liMain.classed("disabled") || liMain.classed("waitForRender") || liMain.classed("error")) {
                            $(this).css("background-color", "#DCDCDC");
                            $(this).find(".fa-trash").css("visibility", "hidden");
                        } else {
                            $(this).css("background-color", "#FFFFFF");
                            $(this).find(".fa-trash").css("visibility", "hidden");
                        }
                    })
                    .on("click", function() {
                        window.location.replace("/account/" + elem["fileName"] + "/")
                    }).text(function() {
                        var text = elem["fileName"];
                        if (elem["rendering"]) {
                            text += " | File is rendering";
                        } else if (elem["renderCount"] == 0) {
                            text += " | Waiting for computation";
                        } else if (!elem["rendered"]) {
                            text += " | Error while rendering";
                        }
                        return text;
                    });

                a.append("i").attr("class", "fa fa-trash fa-1x pull-right")
                    .style("visibility", "hidden")
                    .style("cursor", "pointer")
                    .on("click", function() {
                        if (window.location.pathname != "/account/" + elem["fileName"] + "/") {
                            d3.event.stopPropagation();
                        } else {
                            d3.event.stopPropagation();
                            window.location.replace("/account")
                        }
                        $.ajax({
                            url : "/api/deleteFile",
                            type : "post",
                            contentType: 'application/json; charset=utf-8',
                            data : JSON.stringify({
                                "action" : "delete",
                                "fileName" : elem["fileName"]
                            }),
                            success: function() {
                                updateFilesList();
                            }
                        })
                    });
                count++;
            });
            if (count == 0) {
                fileTable.append("li").append("a").text("You have no files");
            } else {
                fileTable.append("hr");
                fileTable.append("li").append("a").attr("href", "/account/diversity").text("Diversity");
                fileTable.append("li").append("a").attr("href", "/account/basicStats").text("Summary");
            }
            if (count != 0) {
                fileTable.append("hr");
                fileTable.append("li").append("a").attr("href", "/account/deleteAll").text("Delete all");
            }
            if (progressCount != 0) {
                d3.select(".addNewFilesButton").classed("fa-plus", false);
                d3.select(".addNewFilesButton").classed("fa-refresh", true);
            } else {
                d3.select(".addNewFilesButton").classed("fa-plus", true);
                d3.select(".addNewFilesButton").classed("fa-refresh", false);
            }
        });
    }
    var progressCount = 0;
    var filesCount = 0;

    $(".fileNameInput #fileName").keyup(function() {
       var pattern = "^[a-zA-Z0-9_.-]{0,20}$";
       if (!$(this).val().match(pattern)) {
           $(".fileNameInput .regex-error").css("visibility", "visible");
           $(".buttonsInput input").addClass("disabled");
       } else {
           $(".fileNameInput .regex-error").css("visibility", "hidden");
           $(".buttonsInput input").removeClass("disabled");
       }
   });

    $(".fileInput input").change(function() {
       var fileName = $(".fileInput input").val().split(/(\\|\/)/g).pop();
       $(".fileNameInput input").attr("value", fileName.substr(0, fileName.lastIndexOf('.')) || fileName);
   });

    $('#fileupload').fileupload({
        url: '/api/uploadFile',
        dataType: 'json',
        add: function (e, data) {
            var fileName = data.files[0].name;
            var create = true;
            fileName = fileName.substr(0, fileName.lastIndexOf('.')) || fileName;
            $(".fileName").each(function() {
                if ($(this).html() == fileName) {
                    create = false;
                }
            });
            if (create) {
                filesCount++;
                d3.select(".filesTable .table").style("visibility", "visible");
                data.context = d3.select(".filesTable .table tbody.main-tbody-files").append("tr");
                data.context.append("td").attr("class", "fileName")
                    .append("input").attr("type", "text")
                    .attr("class", "fileNameInput")
                    .attr("name", "fileNameInput")
                    .attr("value", fileName);

                var optionValues = data.context.append("td").attr("class", "software-td").append("select")
                    .attr("id", "softwareTypeName").attr("name", "softwareTypeName");

                optionValues.append("option").attr("value", "mitcr").text("MiTcr");
                optionValues.append("option").attr("value", "migec").text("MiGec");
                optionValues.append("option").attr("value", "simple").text("Simple");
                optionValues.append("option").attr("value", "igblast").text("IgBlast");
                optionValues.append("option").attr("value", "cdrblast").text("CdrBlast");

                data.context.append("td").style("width", "40%").attr("class", "progress-td").append("div")
                    .attr("class", "progress progress-striped active")
                    .append("div").attr("class", "progress-bar").style("width", "0%");
                data.context.append("td").attr("class", "tdUploadButton").style("width", "10%")
                    .append("button")
                    .attr("class", "unitFileUpload btn btn-default").text("Upload").on("click", function () {
                        var softwareTypeName = data.context.select("#softwareTypeName").node().value;
                        var inputFileName = data.context.select("td .fileNameInput").node().value;
                        data.context.select("td.fileName").html(inputFileName);
                        data.formData = {softwareTypeName: softwareTypeName, fileName: inputFileName};
                        progressCount++;
                        updateFilesList();
                        data.submit();
                    });
            }
        },
        progress: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 50, 10);
            data.context.select(".tdUploadButton").html("Uploading...")
            data.context.select(".progress-bar").style("width", progress + "%");
        },
        done: function(e, data) {
            progressCount--;
            updateFilesList();
            if (data.result["success"] != undefined) {
                progressCount++;
                data.context.select(".tdUploadButton").html("Computation...");
                var socket = new WebSocket("ws://" + location.host + "/account/" + data.formData.fileName + "/render");
                socket.onmessage = function(event) {
                    if (event.data == "start") {
                        updateFilesList();
                    } else if (event.data != "ComputationDone" && event.data != "ComputationError") {
                        data.context.select(".progress-bar").style("width", 50 + (event.data / 2) + "%");
                    } else if (event.data == "ComputationDone") {
                        data.context.select(".tdUploadButton").html("").append("i").attr("class", "fa  fa-check fa-2x pull-right").style("color", "green");
                        data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-success");
                        data.context.select(".progress").attr("class", "progress progress-striped")
                        data.context.attr("class","success");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    } else {
                        data.context.select(".tdUploadButton").html("").append("i").attr("class", "fa  fa-remove fa-2x pull-right").style("color", "red");
                        data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-danger");
                        data.context.attr("class", "danger computation-fail");
                        data.context.select(".progress-td").html("Computation error");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    }
                };
            } else if (data.result["error"] != undefined) {
                data.context.select(".tdUploadButton").html("").append("i").attr("class", "fa  fa-remove fa-2x pull-right").style("color", "red");;
                data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-danger");
                data.context.attr("class", "danger upload-fail");
                data.context.select(".progress-td").html(data.result["error"]);
            } else {
                data.context.select(".tdUploadButton").html("");
            }
        }
    });

    $(".addNewFilesButton").click(function() {
       $(".newFilesContainer").css("visibility", "visible");
       $(".newFilesContainer").animate({
           opacity: "1"
       },250)
       $(".filesTable").animate({
           top : "20%"
       },400)
   });

    $(".closeButton").click(function() {
       updateFilesList();
       $("tr.success").remove();
       $("tr.danger").remove();
        $(".newFilesContainer").animate({
            opacity: "0"
        },250)
        $(".filesTable").animate({
            top : "-100%"
        },400, function() {
            $(".newFilesContainer").css("visibility", "hidden");
        }, 0)
    });

    $(".uploadAllButton").click(function() {
       $(".unitFileUpload").click();
   });

    $(".chooseFilesButton").click(function() {
      $("form input[type=file]").click();
  });

    $(".commonSoftwareType").change(function() {
      var softwareType = d3.select(".commonSoftwareType").node().value;
      d3.selectAll("#softwareTypeName").selectAll("option").attr("selected", null);
      d3.selectAll("#softwareTypeName").select("option[value="+softwareType+"]").attr("selected", "selected");
  })

    $(".fileDeleteButton").click(function() {
        $.ajax({
            url : "/api/deleteFile",
            type : "post",
            contentType: 'application/json; charset=utf-8',
            data : JSON.stringify({
                "action" : "delete",
                "fileName" : $(this).attr("id")
            }),
            success: function() {
                updateFilesList();
            }
        })
    })
});