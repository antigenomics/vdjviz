$(document).ready(function() {

    if ($(".addNewFilesButton").length != 0) {
        updateFilesList();
    }

    if ($(".mainContent").length != 0) {
        accountPageInitializing();
    }

    var currentFile = "";

    function accountPageInitializing() {
        $.getJSON("/api/accountInformation", function (data) {
            if (!data) {
                window.location.replace("/account")
            }
            clearMainContent();
            var accountInformationTable = d3.select(".mainContent")
                .html("Account Information")
                .append("table")
                .attr("class", "table table-responsive")
                .append("tbody");

            var emailtr = accountInformationTable.append("tr");
                emailtr.append("td")
                    .text("Email: ");
                emailtr.append("td")
                    .text(data["data"]["email"]);

            var firstNametr = accountInformationTable.append("tr");
                firstNametr.append("td")
                    .text("First Name: ");
                firstNametr.append("td")
                    .text(data["data"]["firstName"]);

            var lastNametr = accountInformationTable.append("tr");
                lastNametr.append("td")
                    .text("Last Name: ");
                lastNametr.append("td")
                    .text(data["data"]["lastName"]);

        }).error(function () {
            location.reload();
        });
    }

    function updateFilesList() {
        $.getJSON("/api/allFilesInformation", function(data) {
            if (!data) {
                window.location.replace("/account")
            }
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
                        fileComputationResults(elem["fileName"]);
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

                a.append("i").attr("class", "fa fa-trash pull-right")
                    .style("visibility", "hidden")
                    .style("cursor", "pointer")
                    .on("click", function() {
                        if (currentFile !=  elem["fileName"]) {
                            d3.event.stopPropagation();
                        } else {
                            d3.event.stopPropagation();
                            accountPageInitializing();
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
                fileTable.append("li").append("a")
                    .on("click", function() {
                        diversityStats();
                    })
                    .text("Diversity")
                    .append("i").attr("class", "fa fa-area-chart pull-right");
                fileTable.append("li").append("a")
                    .on("click", function() {
                        basicStats();
                    }).text("Summary")
                    .append("i").attr("class", "fa fa-th-list pull-right");
                fileTable.append("hr");
                fileTable.append("li").append("a").on("click", function() {
                    $.ajax({
                        url : "/api/deleteAll",
                        type : "post",
                        contentType: 'application/json; charset=utf-8',
                        data : JSON.stringify({
                            "action" : "deleteAll"
                        }),
                        success: function(data) {
                            accountPageInitializing();
                            updateFilesList();
                        }
                    })
                }).text("Delete all");
            }
            if (progressCount != 0) {
                d3.select(".addNewFilesButton").classed("fa-plus", false);
                d3.select(".addNewFilesButton").classed("fa-refresh", true);
            } else {
                d3.select(".addNewFilesButton").classed("fa-plus", true);
                d3.select(".addNewFilesButton").classed("fa-refresh", false);
            }
        }).error( function() {
            location.reload();
        });
    }

    function clearVisualisation() {
        d3.select(".visualisation").html("");
    }

    function clearMainContent() {
        d3.select(".mainContent").html("");
    }

    function fileComputationResults(fileName) {
        currentFile = fileName;
        d3.select(".mainContent").html("");
        loading();
        var header = d3.select(".mainContent")
            .append("ul")
            .attr("class", "nav nav-pills")
            .style("cursor", "pointer");

            header.append("li")
                .style("width", "19%")
                .attr("class", "computationResultsButton")
                .append("a")
                .attr("class", "text-center")
                .on("click", function() {
                    clearVisualisation();
                    d3.selectAll(".computationResultsButton").classed("active", false);
                    d3.select(this.parentNode).classed("active", true);
                    VJUsage("/api/" + fileName + "/vjusage");
                })
                .html("V-J Usage");
            header.append("li")
                .attr("class", "active computationResultsButton")
                .style("width", "19%")
                .append("a")
                .attr("class", "text-center")
                .on("click", function() {
                    clearVisualisation();
                    d3.selectAll(".computationResultsButton").classed("active", false);
                    d3.select(this.parentNode).classed("active", true);
                    HistogramData("/api/" + fileName + "/histogram");
                })
                .html("Spectrotype")
                .append("i")
                .attr("class", "fa fa-bar-chart-o pull-right");
            header.append("li")
                .style("width", "19%").
                attr("class", "computationResultsButton")
                .append("a")
                .attr("class", "text-center")
                .on("click", function() {
                    clearVisualisation();
                    d3.selectAll(".computationResultsButton").classed("active", false);
                    d3.select(this.parentNode).classed("active", true);
                    HistogramVData("/api/" + fileName + "/histogramV");
                })
                .html("SpectrotypeV")
                .append("i")
                .attr("class", "fa fa-bar-chart-o pull-right");
            header.append("li")
                .style("width", "19%")
                .attr("class", "computationResultsButton")
                .append("a")
                .attr("class", "text-center")
                .on("click", function() {
                    clearVisualisation();
                    d3.selectAll(".computationResultsButton").classed("active", false);
                    d3.select(this.parentNode).classed("active", true);
                    renderKernelDensity("/api/" + fileName + "/kernelDensity");
                })
                .html("Kernel Density");
            header.append("li")
                .style("width", "19%")
                .attr("class", "computationResultsButton")
                .append("a")
                .attr("class", "text-center")
                .on("click", function() {
                    clearVisualisation();
                    d3.selectAll(".computationResultsButton").classed("active", false);
                    d3.select(this.parentNode).classed("active", true);
                    AnnotationTable("/api/" + fileName + "/annotation");
                })
                .html("Annotation")
                .append("i")
                .attr("class", "fa fa-th-list pull-right");

        d3.select(".mainContent").append("div")
            .attr("class", "hero-unit")
            .append("div")
            .attr("class", "visualisation")
            .style("opacity", "0")
            .style("top", "50px")
            .style("width", "100%")
            .style("position", "relative")
            .style("height", "50%");

        HistogramData("/api/" + fileName + "/histogram");
        loaded();
        d3.select(".visualisation")
            .transition()
            .style("opacity", "1");
    }

    function diversityStats() {
        clearMainContent();
        d3.select(".mainContent").append("div")
            .attr("class", "hero-unit")
            .append("div").attr("class", "visualisation")
            .style("top", "50px").style("width", "100%")
            .style("position", "relative").style("height", "50%")
            .style("opacity", "0")
            .append("div").attr("id", "chart")
            .append("svg").attr("height", "800px").attr("width", "100%")
            .style("overflow", "visible");
        renderLineChart("/api/diversity");
    }

    function basicStats() {
        clearMainContent();
        d3.select(".mainContent").append("div")
            .attr("class", "hero-unit")
            .append("div").attr("class", "visualisation")
            .style("top", "50px").style("width", "100%")
            .style("opacity", "0")
            .style("position", "relative").style("height", "50%");

        BasicStatsTable("/api/getBasicStats");
    }

    function loading() {
        var loading = d3.select(".mainContent").append("div").attr("class", "loading");
        loading.append("div").attr("class", "wBall").attr("id", "wBall_1").append("div").attr("class", "wInnerBall");
        loading.append("div").attr("class", "wBall").attr("id", "wBall_2").append("div").attr("class", "wInnerBall");
        loading.append("div").attr("class", "wBall").attr("id", "wBall_3").append("div").attr("class", "wInnerBall");
        loading.append("div").attr("class", "wBall").attr("id", "wBall_4").append("div").attr("class", "wInnerBall");
        loading.append("div").attr("class", "wBall").attr("id", "wBall_5").append("div").attr("class", "wInnerBall");
    }

    function loaded() {
        d3.select(".loading").remove();
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
            var originalFileName = data.files[0].name;
            var create = true;
            var fileName = originalFileName.substr(0, originalFileName.lastIndexOf('.')) || originalFileName;
            var fileExtension = originalFileName.substr((~-originalFileName.lastIndexOf(".") >>> 0) + 2);
            if (fileExtension != "txt" && fileExtension != "gz") {
                fileName += fileExtension;
                fileExtension = "txt";
            }
            $(".fileName").each(function() {
                if ($(this).html() == fileName) {
                    create = false;
                }
            });
            if (create) {
                filesCount++;
                d3.select(".filesTable .table")
                    .style("visibility", "visible");

                data.context = d3.select(".filesTable .table tbody.main-tbody-files")
                    .append("tr");

                data.context.append("td")
                    .attr("class", "fileName")
                    .append("input")
                    .attr("type", "text")
                    .attr("class", "fileNameInput")
                    .attr("name", "fileNameInput")
                    .attr("value", fileName);

                var optionValues = data.context.append("td")
                    .attr("class", "software-td")
                    .append("select")
                    .attr("id", "softwareTypeName")
                    .attr("name", "softwareTypeName");

                optionValues.append("option").attr("value", "mitcr").text("MiTcr");
                optionValues.append("option").attr("value", "migec").text("MiGec");
                optionValues.append("option").attr("value", "simple").text("Simple");
                optionValues.append("option").attr("value", "igblast").text("IgBlast");
                optionValues.append("option").attr("value", "cdrblast").text("CdrBlast");

                data.context.append("td")
                    .style("width", "40%")
                    .attr("class", "progress-td")
                    .append("div")
                    .attr("class", "progress progress-striped active")
                    .append("div")
                    .attr("class", "progress-bar")
                    .style("width", "0%");

                data.context.append("td")
                    .attr("class", "tdUploadButton")
                    .style("width", "10%")
                    .append("button")
                    .attr("class", "unitFileUpload btn btn-default")
                    .text("Upload")
                    .on("click", function () {
                        var softwareTypeName = data.context.select("#softwareTypeName").node().value;
                        var inputFileName = data.context.select("td .fileNameInput").node().value;
                        data.context.select("td.fileName").html(inputFileName);
                        data.context.select("td.software-td").html(softwareTypeName);
                        data.formData = {
                            softwareTypeName: softwareTypeName,
                            fileName: inputFileName,
                            fileExtension: fileExtension
                        };
                        progressCount++;
                        updateFilesList();
                        data.submit();
                    });
            }
        },
        progress: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 50, 10);
            data.context.select(".tdUploadButton")
                .html("Uploading...")
            data.context.select(".progress-bar")
                .style("width", progress + "%");
        },
        done: function(e, data) {
            progressCount--;
            updateFilesList();
            if (data.result["success"] != undefined) {
                progressCount++;
                data.context.select(".tdUploadButton")
                    .html("Computation...");
                var socket = new WebSocket("ws://" + location.host + "/account/" + data.formData.fileName + "/render");
                socket.onmessage = function(event) {
                    if (event.data == "start") {
                        updateFilesList();
                    } else if (event.data != "ComputationDone" && event.data != "ComputationError") {
                        data.context.select(".progress-bar")
                            .style("width", 50 + (event.data / 2) + "%");
                    } else if (event.data == "ComputationDone") {
                        data.context.select(".tdUploadButton")
                            .html("")
                            .append("i")
                            .attr("class", "fa  fa-check fa-2x pull-right")
                            .style("color", "green");
                        data.context.select(".progress-bar")
                            .attr("class", "progress-bar progress-bar-success");
                        data.context.select(".progress")
                            .attr("class", "progress progress-striped");
                        data.context
                            .attr("class","success");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    } else {
                        data.context.select(".tdUploadButton")
                            .html("")
                            .append("i")
                            .attr("class", "fa  fa-remove fa-2x pull-right")
                            .style("color", "red");
                        data.context.select(".progress-bar")
                            .attr("class", "progress-bar progress-bar-danger");
                        data.context
                            .attr("class", "danger computation-fail");
                        data.context.select(".progress-td")
                            .html("Computation error");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    }
                };
            } else if (data.result["error"] != undefined) {
                data.context.select(".tdUploadButton")
                    .html("").append("i")
                    .attr("class", "fa  fa-remove fa-2x pull-right")
                    .style("color", "red");;
                data.context.select(".progress-bar")
                    .attr("class", "progress-bar progress-bar-danger");
                data.context
                    .attr("class", "danger upload-fail");
                data.context
                    .select(".progress-td")
                    .html(data.result["error"]);
            } else {
                data.context.select(".tdUploadButton")
                    .html("");
            }
        }
    });

    $(".addNewFilesButton").click(function() {
       $(".newFilesContainer").css("visibility", "visible");
       $(".newFilesContainer").animate({
           opacity: "1"
       },250)
       $(".newFilesContainer .filesTable").animate({
           top : "20%"
       },400)
    });

    $(".newFilesContainer .closeButton").click(function() {
        updateFilesList();
        $(".newFilesContainer tr.success").remove();
        $(".newFilesContainer tr.danger").remove();
        $(".newFilesContainer").animate({
            opacity: "0"
        },250)
        $(".newFilesContainer .filesTable").animate({
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
      d3.selectAll("#softwareTypeName")
          .selectAll("option")
          .attr("selected", null);
      d3.selectAll("#softwareTypeName")
          .select("option[value="+softwareType+"]")
          .attr("selected", "selected");
  });


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