$(document).ready(function() {
    //updateFilesList();
    var progressCount = 0;
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
        url: '/account/asyncUpload',
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
                        data.context.select(".tdUploadButton").html("Computation success");
                        data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-success");
                        data.context.select(".progress").attr("class", "progress progress-striped")
                        data.context.attr("class","success");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    } else {
                        data.context.select(".tdUploadButton").html("");
                        data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-danger");
                        data.context.attr("class", "danger computation-fail");
                        data.context.select(".progress-td").html("Computation error");
                        progressCount--;
                        updateFilesList();
                        socket.close();
                    }
                };
            } else if (data.result["error"] != undefined) {
                data.context.select(".tdUploadButton").html("");
                data.context.select(".progress-bar").attr("class", "progress-bar progress-bar-danger");
                data.context.attr("class", "danger upload-fail");
                data.context.select(".progress-td").html(data.result["error"]);
            } else {
                data.context.select(".tdUploadButton").html("");
            }
        }
    });


   var filesCount = 0;
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

  function updateFilesList() {
      $.getJSON("/account/allFiles", function(data) {
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
                   liMain.append("a").attr("href", "/account/" + elem + "/")
                   .attr("class", "dropdown-toggle disabled")
                   .attr("data-toggle", "dropdown")
                   .attr("href", function() {
                           if (!elem["rendering"]) {
                               return "/account/" + elem["fileName"] + "/";
                           } else {
                               return null;
                           }
                   })
                   .text(function() {
                        var text = elem["fileName"];
                        if (elem["rendering"]) {
                            text += " | File is rendering";
                        } else if (elem["renderCount"] == 0) {
                            text += " | Waiting for computation";
                        } else if (!elem["rendered"]) {
                            text += " | Error while rendering";
                        }
                    return text;
                   }).style("background-color", function() {
                        if (elem["rendering"] || !elem["rendered"]) {
                            return "#DCDCDC";
                        }
                        return "#FFFFFF";
                   }).on("mouseover", function() {
                           $(this).css("background-color", "#DCDCDC")
                   }).on("mouseout", function() {
                        if (liMain.classed("disabled") || liMain.classed("waitForRender") || liMain.classed("error")) {
                            $(this).css("background-color", "#DCDCDC")
                        } else {
                            $(this).css("background-color", "#FFFFFF")
                        }
                   });
              if (!elem["rendering"]) {
                  var ul = liMain.append("ul").attr("class", "dropdown-menu");
                  ul.append("li").append("a").attr("href", "/account/" + elem["fileName"] + "/").text("Information");
                  ul.append("li").append("a").attr("href", "/account/" + elem["fileName"] + "/update").text("Update");
                  ul.append("li").attr("class", "divider");
                  ul.append("li").append("a").attr("href", "/account/" + elem["fileName"] + "/delete").text("Delete");
              }
              count++;
          });
          if (count == 0) {
              fileTable.append("li").append("a").text("You have no files");
          } else {
              fileTable.append("hr");
              fileTable.append("li").append("a").attr("href", "/account/diversity").text("Diversity");
              fileTable.append("li").append("a").attr("href", "/account/basicStats").text("Summary");
          }
          fileTable.append("hr");
          fileTable.append("li").style("cursor", "pointer").append("a").attr("class", "addNewFilesButton").text(function () {
              var text = "Add";
              if (progressCount != 0) {
                  text += " | in progress";
              }
              return text;
          })
              .on("click",function() {
                    $(".newFilesContainer").css("visibility", "visible");
                    $(".newFilesContainer").animate({
                        opacity: "1"
                    },250)
                    $(".filesTable").animate({
                        top : "20%"
                    },400)});
          fileTable.append("li").append("a").attr("href", "/account/deleteAll").text("Delete all");
      });
      }


  $(".commonSoftwareType").change(function() {
      var softwareType = d3.select(".commonSoftwareType").node().value;
      d3.selectAll("#softwareTypeName").selectAll("option").attr("selected", null);
      d3.selectAll("#softwareTypeName").select("option[value="+softwareType+"]").attr("selected", "selected");
  })
});