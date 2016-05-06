(function() {
    var application = angular.module('vidjilTest', []);

    application.factory('load', ['$http', function($http) {

        function loadSingle() {
            console.log(single);
            $http.post('/vidjil/api/share', { token: '', json: single, sampleCount: 1 })
                .success(function(response) {
                    console.log('success');
                    console.log(response);
                })
                .error(function(response) {
                    console.log('error');
                    console.log(response);
                })
        }

        return {
            loadSingle: loadSingle
        }
    }]);

    application.directive('asd', function() {
        return {
            restrict: 'E',
            controller: ['$scope', 'load', function($scope, load) {
                $scope.loadS = load.loadSingle;
            }]
        }
    })
}());

var single = {
    "clones": [
        {
            "_average_read_length": [
                317.347198486328
            ],
            "_coverage": [
                1.00205707550049
            ],
            "_coverage_info": [
                "318 bp (100% of 317.3 bp)"
            ],
            "germline": "IGH",
            "id": "CACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAAG",
            "name": "IGHV3-9*01 7/CCCGGA/17 IGHJ6*02",
            "reads": [
                189991
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 282,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 275,
                "N": 6,
                "_evalue": "1.365024e-66",
                "_evalue_left": "1.365024e-66",
                "_evalue_right": "6.267272e-202",
                "affectSigns": {
                    "seq": "------------------------                 --------------------------------------------------------------------------------------                                                                                                                                                                                   ",
                    "start": 0,
                    "stop": 318
                },
                "affectValues": {
                    "seq": "hhhhhhhhhhhhhhhhhhhhhhhh_________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH___________________________________________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 318
                },
                "cdr3": {
                    "aa": "APGGMDV",
                    "start": 274,
                    "stop": 294
                },
                "junction": {
                    "aa": "CAPGGMDVW",
                    "productive": true,
                    "start": 271,
                    "stop": 297
                }
            },
            "seg_stat": {
                "2": 55411,
                "3": 134580
            },
            "sequence": "GGAGTCGGGGGAGGCTTGGTACAGCCTGGCAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACC",
            "top": 1
        },
        {
            "_average_read_length": [
                173.038192749023
            ],
            "_coverage": [
                1.01711654663086
            ],
            "_coverage_info": [
                "176 bp (101% of 173.0 bp)"
            ],
            "germline": "TRG",
            "id": "AAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTGC",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                174044
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 116,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 115,
                "N": 0,
                "_evalue": "4.644255e-109",
                "_evalue_left": "4.644255e-109",
                "_evalue_right": "1.084264e-185",
                "affectSigns": {
                    "seq": "     ---------------------------------------------            ------------------------------------------------------------------------------             -            ",
                    "start": 0,
                    "stop": 176
                },
                "affectValues": {
                    "seq": "_____ggggggggggggggggggggggggggggggggggggggggggggg____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_____________G____________",
                    "start": 0,
                    "stop": 176
                },
                "cdr3": {
                    "aa": "A#TGWFKI",
                    "start": 113,
                    "stop": 134
                },
                "junction": {
                    "aa": "CA#TGWFKIF",
                    "productive": false,
                    "start": 110,
                    "stop": 137
                }
            },
            "seg_stat": {
                "2": 95284,
                "3": 78760
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAAA",
            "top": 2
        },
        {
            "_average_read_length": [
                180.988403320312
            ],
            "_coverage": [
                1.00006401538849
            ],
            "_coverage_info": [
                "181 bp (100% of 181.0 bp)"
            ],
            "germline": "TRG",
            "id": "GCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTGC",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                131526
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 125,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 124,
                "N": 0,
                "_evalue": "1.273181e-58",
                "_evalue_left": "1.106911e-199",
                "_evalue_right": "1.273181e-58",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                ++++++++             +++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 181
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________________gggggggg_____________ggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 181
                },
                "cdr3": {
                    "aa": "AACL#DWIKT",
                    "start": 112,
                    "stop": 140
                },
                "junction": {
                    "aa": "CAACL#DWIKTF",
                    "productive": false,
                    "start": 109,
                    "stop": 143
                }
            },
            "seg_stat": {
                "2": 131526
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTGCAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 3
        },
        {
            "_average_read_length": [
                174.651840209961
            ],
            "_coverage": [
                1.00199341773987
            ],
            "_coverage_info": [
                "175 bp (100% of 174.7 bp)"
            ],
            "germline": "TRG",
            "id": "AGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTGC",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                100355
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 116,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 115,
                "N": 0,
                "_evalue": "6.262732e-110",
                "_evalue_left": "1.056383e-163",
                "_evalue_right": "6.262732e-110",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++            +++++++++++++++++++++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 175
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGG____________ggggggggggggggggggggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 175
                },
                "cdr3": {
                    "aa": "A#TGWFKI",
                    "start": 113,
                    "stop": 134
                },
                "junction": {
                    "aa": "CA#TGWFKIF",
                    "productive": false,
                    "start": 110,
                    "stop": 137
                }
            },
            "seg_stat": {
                "2": 100354,
                "3": 1
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 4
        },
        {
            "_average_read_length": [
                182.104598999023
            ],
            "_coverage": [
                0.993934273719788
            ],
            "_coverage_info": [
                "181 bp (99% of 182.1 bp)"
            ],
            "germline": "TRG",
            "id": "CCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTGC",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                98122
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 125,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 124,
                "N": 0,
                "_evalue": "1.273181e-58",
                "_evalue_left": "9.595058e-178",
                "_evalue_right": "1.273181e-58",
                "affectSigns": {
                    "seq": "           +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++++++++                ++++++++             +++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 181
                },
                "affectValues": {
                    "seq": "___________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGGGGGGGG________________gggggggg_____________ggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 181
                },
                "cdr3": {
                    "aa": "AACL#DWIKT",
                    "start": 112,
                    "stop": 140
                },
                "junction": {
                    "aa": "CAACL#DWIKTF",
                    "productive": false,
                    "start": 109,
                    "stop": 143
                }
            },
            "seg_stat": {
                "2": 98122
            },
            "sequence": "GCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTGCAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 5
        },
        {
            "_average_read_length": [
                180.354309082031
            ],
            "_coverage": [
                0.837240874767303
            ],
            "_coverage_info": [
                "151 bp (83% of 180.4 bp)"
            ],
            "germline": "TRG",
            "id": "GCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTGCA",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                96939
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 94,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 93,
                "N": 0,
                "_evalue": "4.731133e-98",
                "_evalue_left": "4.731133e-98",
                "_evalue_right": "1.504542e-184",
                "affectSigns": {
                    "seq": "     ------------------------------------------                --------------------------------------------------------------------------    ",
                    "start": 0,
                    "stop": 151
                },
                "affectValues": {
                    "seq": "_____gggggggggggggggggggggggggggggggggggggggggg________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG____",
                    "start": 0,
                    "stop": 151
                },
                "cdr3": {
                    "aa": "AACL#DWIKT",
                    "start": 81,
                    "stop": 109
                },
                "junction": {
                    "aa": "CAACL#DWIKTF",
                    "productive": false,
                    "start": 78,
                    "stop": 112
                }
            },
            "seg_stat": {
                "2": 13170,
                "3": 83769
            },
            "sequence": "AAGAAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAAA",
            "top": 6
        },
        {
            "_average_read_length": [
                219.513458251953
            ],
            "_coverage": [
                0.95666116476059
            ],
            "_coverage_info": [
                "210 bp (95% of 219.5 bp)"
            ],
            "germline": "TRG",
            "id": "GCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAAACTCTTTGGCA",
            "name": "TRGV2*02 0/GGTCTACCCCCACG/2 TRGJ1*01",
            "reads": [
                90957
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 2,
                "3start": 176,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 161,
                "N": 14,
                "_evalue": "8.919077e-20",
                "_evalue_left": "8.919077e-20",
                "_evalue_right": "8.456641e-192",
                "affectSigns": {
                    "seq": "-------------                                 ----------------------------------------------------------------------------------------- --                                                              ",
                    "start": 0,
                    "stop": 210
                },
                "affectValues": {
                    "seq": "ggggggggggggg_________________________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG______________________________________________________________",
                    "start": 0,
                    "stop": 210
                },
                "cdr3": {
                    "aa": "ATWDGLPPR#YIRKL",
                    "start": 151,
                    "stop": 193
                },
                "junction": {
                    "aa": "CATWDGLPPR#YIRKLF",
                    "productive": false,
                    "start": 148,
                    "stop": 196
                }
            },
            "seg_stat": {
                "2": 197,
                "3": 90760
            },
            "sequence": "CGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAAACTCTTTGGCAGTGGAACAAC",
            "top": 7
        },
        {
            "_average_read_length": [
                222.189926147461
            ],
            "_coverage": [
                1.01264715194702
            ],
            "_coverage_info": [
                "225 bp (101% of 222.2 bp)"
            ],
            "germline": "TRG",
            "id": "CTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTTG",
            "name": "TRGV2*02 0/GGTCTACCCCCACG/2 TRGJ1*01",
            "reads": [
                84671
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 2,
                "3start": 190,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 175,
                "N": 14,
                "_evalue": "4.860834e-41",
                "_evalue_left": "1.112976e-189",
                "_evalue_right": "4.860834e-41",
                "affectSigns": {
                    "seq": "                                                                            ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                          +++++++++++++++++++++",
                    "start": 0,
                    "stop": 225
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG__________________________ggggggggggggggggggggg",
                    "start": 0,
                    "stop": 225
                },
                "cdr3": {
                    "aa": "ATWDGLPPRLYKKL",
                    "start": 165,
                    "stop": 206
                },
                "junction": {
                    "aa": "CATWDGLPPRLYKKLF",
                    "productive": true,
                    "start": 162,
                    "stop": 209
                }
            },
            "seg_stat": {
                "2": 58654,
                "3": 26017
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 8
        },
        {
            "_average_read_length": [
                245.375274658203
            ],
            "_coverage": [
                1.01477217674255
            ],
            "_coverage_info": [
                "249 bp (101% of 245.4 bp)"
            ],
            "germline": "TRG",
            "id": "ATTACTGTGCCACCTGGGACCCCCGGGGCGGGTAGTGATTGGATCAAGAC",
            "name": "TRGV3*01 3/CCCCGGGGCGG/3 TRGJP2*01",
            "reads": [
                64944
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 3,
                "3start": 187,
                "5": "TRGV3*01",
                "5del": 3,
                "5end": 175,
                "N": 11,
                "_evalue": "3.927311e-108",
                "_evalue_left": "1.620260e-183",
                "_evalue_right": "3.927311e-108",
                "affectSigns": {
                    "seq": "                                                                             + +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                     +++++++++++++++++++++++++++++++++++++++++++++++     ",
                    "start": 0,
                    "stop": 249
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________G?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_____________________ggggggggggggggggggggggggggggggggggggggggggggggg_____",
                    "start": 0,
                    "stop": 249
                },
                "cdr3": {
                    "aa": "ATWDPRGG#SDWIKT",
                    "start": 165,
                    "stop": 207
                },
                "junction": {
                    "aa": "CATWDPRGG#SDWIKTF",
                    "productive": false,
                    "start": 162,
                    "stop": 210
                }
            },
            "seg_stat": {
                "2": 40153,
                "3": 24791
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACCCCCGGGGCGGGTAGTGATTGGATCAAGACGTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAAA",
            "top": 9
        },
        {
            "_average_read_length": [
                333.862731933594
            ],
            "_coverage": [
                1.04833507537842
            ],
            "_coverage_info": [
                "350 bp (104% of 333.9 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAG",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                46870
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 306,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 250,
                "N": 55,
                "_evalue": "5.547297e-74",
                "_evalue_left": "5.547297e-74",
                "_evalue_right": "8.024297e-207",
                "affectSigns": {
                    "seq": "--------------------------------                                                                   -----------------------------------------------------------------------------------------                                                                                                                                                      ",
                    "start": 0,
                    "stop": 350
                },
                "affectValues": {
                    "seq": "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh___________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH______________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 350
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKQTGPSGGMDV",
                    "start": 245,
                    "stop": 319
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKQTGPSGGMDVW",
                    "productive": true,
                    "start": 242,
                    "stop": 322
                }
            },
            "seg_stat": {
                "2": 3051,
                "3": 43819
            },
            "sequence": "CTGGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCT",
            "top": 10
        },
        {
            "_average_read_length": [
                313.383941650391
            ],
            "_coverage": [
                0.928573429584503
            ],
            "_coverage_info": [
                "291 bp (92% of 313.4 bp)"
            ],
            "germline": "IGH",
            "id": "AAAAGATATTCTTAAATCACTTAAGCAGCAGCTGGCCACCCCGAACTGGT",
            "name": "IGHV3-9*01 0/TTCTTAAATCACTTA/6 IGHD6-13*01 3/CCACCCCG/2 IGHJ5*02",
            "reads": [
                27359
            ],
            "seg": {
                "3": "IGHJ5*02",
                "3del": 2,
                "3start": 240,
                "4": "IGHD6-13*01",
                "4delLeft": 6,
                "4delRight": 3,
                "4end": 231,
                "4start": 220,
                "5": "IGHV3-9*01",
                "5del": 0,
                "5end": 204,
                "N1": 15,
                "N2": 8,
                "_evalue": "7.494576e-93",
                "_evalue_left": "7.494576e-93",
                "_evalue_right": "4.450546e-228",
                "affectSigns": {
                    "seq": "  -------------------------------------                                               --------------------------------------------------------------------------------------------                                                                                                     ",
                    "start": 0,
                    "stop": 291
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh_______________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_____________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 291
                },
                "cdr3": {
                    "aa": "AKDILKSLKQQLATPNWFDP",
                    "start": 196,
                    "stop": 255
                },
                "junction": {
                    "aa": "CAKDILKSLKQQLATPNWFDPW",
                    "productive": true,
                    "start": 193,
                    "stop": 258
                }
            },
            "seg_stat": {
                "2": 2418,
                "3": 24941
            },
            "sequence": "TATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCAAAAGATATTCTTAAATCACTTAAGCAGCAGCTGGCCACCCCGAACTGGTTCGACCCCTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 11
        },
        {
            "_average_read_length": [
                190.027389526367
            ],
            "_coverage": [
                1.00511825084686
            ],
            "_coverage_info": [
                "191 bp (100% of 190.0 bp)"
            ],
            "germline": "TRG",
            "id": "ATGACTCTGGGGTCTATTACTGTGAGGGCTCTTTGGCAGTGGAACAACAC",
            "name": "TRGV2*02 11//11 TRGJ1*01",
            "reads": [
                25407
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 11,
                "3start": 165,
                "5": "TRGV2*02",
                "5del": 11,
                "5end": 164,
                "N": 0,
                "_evalue": "4.092749e-23",
                "_evalue_left": "2.124841e-161",
                "_evalue_right": "4.092749e-23",
                "affectSigns": {
                    "seq": "                                                                            ++ ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++              ++++++++++++",
                    "start": 0,
                    "stop": 191
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG______________gggggggggggg",
                    "start": 0,
                    "stop": 191
                },
                "cdr3": {
                    "aa": "#GL",
                    "start": 165,
                    "stop": 172
                },
                "junction": {
                    "aa": "C#GLF",
                    "productive": false,
                    "start": 162,
                    "stop": 175
                }
            },
            "seg_stat": {
                "2": 9280,
                "3": 16127
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGAGGGCTCTTTGGCAGTGGAACAACAC",
            "top": 12
        },
        {
            "_average_read_length": [
                311.916442871094
            ],
            "_coverage": [
                1.02270984649658
            ],
            "_coverage_info": [
                "319 bp (102% of 311.9 bp)"
            ],
            "germline": "IGH",
            "id": "AGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAG",
            "name": "IGHV3-13*05 1/GAGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                16947
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 270,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 215,
                "N": 54,
                "_evalue": "2.626629e-82",
                "_evalue_left": "6.711997e-213",
                "_evalue_right": "2.626629e-82",
                "affectSigns": {
                    "seq": "                                                                                                                   +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                                                                  +++++++++++++++++++++++++++++++++++  ",
                    "start": 0,
                    "stop": 319
                },
                "affectValues": {
                    "seq": "___________________________________________________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH__________________________________________________________________hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 319
                },
                "cdr3": {
                    "aa": "ARGGGLPPPL*PVKSKLGPA#GMDV",
                    "start": 210,
                    "stop": 283
                },
                "junction": {
                    "aa": "CARGGGLPPPL*PVKSKLGPA#GMDVW",
                    "productive": false,
                    "start": 207,
                    "stop": 286
                }
            },
            "seg_stat": {
                "2": 1459,
                "3": 15488
            },
            "sequence": "GGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 13
        },
        {
            "_average_read_length": [
                345.582611083984
            ],
            "_coverage": [
                1.03014445304871
            ],
            "_coverage_info": [
                "356 bp (103% of 345.6 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCA",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                16124
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 307,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 250,
                "N": 56,
                "_evalue": "6.790562e-82",
                "_evalue_left": "6.790562e-82",
                "_evalue_right": "1.148491e-206",
                "affectSigns": {
                    "seq": "  -----------------------------------                                                                    -----------------------------------------------------------------------------------------                                                                                                                                                      ",
                    "start": 0,
                    "stop": 356
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh____________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH______________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 356
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKQNWAQR#GMDV",
                    "start": 245,
                    "stop": 320
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKQNWAQR#GMDVW",
                    "productive": false,
                    "start": 242,
                    "stop": 323
                }
            },
            "seg_stat": {
                "2": 8,
                "3": 16116
            },
            "sequence": "CTGGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 14
        },
        {
            "_average_read_length": [
                244.063629150391
            ],
            "_coverage": [
                1.01612842082977
            ],
            "_coverage_info": [
                "248 bp (101% of 244.1 bp)"
            ],
            "germline": "TRG",
            "id": "CTATTACTGTGCCACCTGGGACCTTGATGATAGTAGTGATTGGATCAAGA",
            "name": "TRGV3*01 3/CTTGATG/0 TRGJP2*01",
            "reads": [
                14302
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 0,
                "3start": 183,
                "5": "TRGV3*01",
                "5del": 3,
                "5end": 175,
                "N": 7,
                "_evalue": "1.295000e-118",
                "_evalue_left": "1.007967e-184",
                "_evalue_right": "1.295000e-118",
                "affectSigns": {
                    "seq": "                                                                             + +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                 ++++++++++++++++++++++++++++++++++++++++++++++++++     ",
                    "start": 0,
                    "stop": 248
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________G?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_________________gggggggggggggggggggggggggggggggggggggggggggggggggg_____",
                    "start": 0,
                    "stop": 248
                },
                "cdr3": {
                    "aa": "ATWDLDDSSDWIKT",
                    "start": 165,
                    "stop": 206
                },
                "junction": {
                    "aa": "CATWDLDDSSDWIKTF",
                    "productive": true,
                    "start": 162,
                    "stop": 209
                }
            },
            "seg_stat": {
                "2": 9145,
                "3": 5157
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACCTTGATGATAGTAGTGATTGGATCAAGACGTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAAA",
            "top": 15
        },
        {
            "_average_read_length": [
                215.329620361328
            ],
            "_coverage": [
                1.00775730609894
            ],
            "_coverage_info": [
                "217 bp (100% of 215.3 bp)"
            ],
            "germline": "TRG",
            "id": "CTATTACTGTGCCACCCCTCCTGCTCATTCCACGAGAGAAACTCTTTGGC",
            "name": "TRGV2*02 6/CCTCCTGCTCATTCCACGAG/8 TRGJ1*02",
            "reads": [
                11149
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 8,
                "3start": 190,
                "5": "TRGV2*02",
                "5del": 6,
                "5end": 169,
                "N": 20,
                "_evalue": "6.065270e-30",
                "_evalue_left": "6.065270e-30",
                "_evalue_right": "4.244054e-169",
                "affectSigns": {
                    "seq": "-----------------                              --------------------------------------------------------------------------------- --                                                                            ",
                    "start": 0,
                    "stop": 217
                },
                "affectValues": {
                    "seq": "ggggggggggggggggg______________________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 217
                },
                "cdr3": {
                    "aa": "ATPPAHSTR#KL",
                    "start": 165,
                    "stop": 198
                },
                "junction": {
                    "aa": "CATPPAHSTR#KLF",
                    "productive": false,
                    "start": 162,
                    "stop": 201
                }
            },
            "seg_stat": {
                "2": 5095,
                "3": 6054
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCCCTCCTGCTCATTCCACGAGAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 16
        },
        {
            "_average_read_length": [
                205.846221923828
            ],
            "_coverage": [
                1.00560510158539
            ],
            "_coverage_info": [
                "207 bp (100% of 205.8 bp)"
            ],
            "germline": "TRG",
            "id": "TGGGGTCTATTACTGTGCCACCTTCTGACATAAGAAACTCTTTGGCAGTG",
            "name": "TRGV3*01 2//6 TRGJ1*02",
            "reads": [
                10866
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 6,
                "3start": 178,
                "5": "TRGV3*01",
                "5del": 2,
                "5end": 177,
                "N": 0,
                "_evalue": "4.991660e-43",
                "_evalue_left": "4.991660e-43",
                "_evalue_right": "9.431766e-173",
                "affectSigns": {
                    "seq": "--------------------                ---------------------------------------------------------------------------------- -                                                                             ",
                    "start": 0,
                    "stop": 207
                },
                "affectValues": {
                    "seq": "gggggggggggggggggggg________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?G_____________________________________________________________________________",
                    "start": 0,
                    "stop": 207
                },
                "cdr3": {
                    "aa": "ATF*HKKL",
                    "start": 165,
                    "stop": 188
                },
                "junction": {
                    "aa": "CATF*HKKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 191
                }
            },
            "seg_stat": {
                "2": 4844,
                "3": 6022
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTTCTGACATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 17
        },
        {
            "_average_read_length": [
                175.545074462891
            ],
            "_coverage": [
                1.00259149074554
            ],
            "_coverage_info": [
                "176 bp (100% of 175.5 bp)"
            ],
            "germline": "TRG",
            "id": "AGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTTG",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                9773
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 116,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 115,
                "N": 0,
                "_evalue": "1.110495e-85",
                "_evalue_left": "1.056383e-163",
                "_evalue_right": "1.110495e-85",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++            +++++++++++        +++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 176
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGG____________ggggggggggg________ggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 176
                },
                "cdr3": {
                    "aa": "A#TGWFKI",
                    "start": 113,
                    "stop": 134
                },
                "junction": {
                    "aa": "CA#TGWFKIF",
                    "productive": false,
                    "start": 110,
                    "stop": 137
                }
            },
            "seg_stat": {
                "2": 9773
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 18
        },
        {
            "_average_read_length": [
                225.519012451172
            ],
            "_coverage": [
                1.00213277339935
            ],
            "_coverage_info": [
                "226 bp (100% of 225.5 bp)"
            ],
            "germline": "TRG",
            "id": "CTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTTT",
            "name": "TRGV2*02 0/GGTCTACCCCCACG/2 TRGJ1*01",
            "reads": [
                9102
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 2,
                "3start": 190,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 175,
                "N": 14,
                "_evalue": "6.597434e-22",
                "_evalue_left": "1.112976e-189",
                "_evalue_right": "6.597434e-22",
                "affectSigns": {
                    "seq": "                                                                            ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                          +++++        +++++++++",
                    "start": 0,
                    "stop": 226
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG__________________________ggggg________ggggggggg",
                    "start": 0,
                    "stop": 226
                },
                "cdr3": {
                    "aa": "ATWDGLPPRLYKKL",
                    "start": 165,
                    "stop": 206
                },
                "junction": {
                    "aa": "CATWDGLPPRLYKKLF",
                    "productive": true,
                    "start": 162,
                    "stop": 209
                }
            },
            "seg_stat": {
                "2": 9101,
                "3": 1
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTTTGGCAGTGGAACAACAC",
            "top": 19
        },
        {
            "_average_read_length": [
                181.020263671875
            ],
            "_coverage": [
                0.82311224937439
            ],
            "_coverage_info": [
                "149 bp (82% of 181.0 bp)"
            ],
            "germline": "TRG",
            "id": "CCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTGCA",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                9079
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 126,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 125,
                "N": 0,
                "_evalue": "1.076939e-24",
                "_evalue_left": "2.923896e-177",
                "_evalue_right": "1.076939e-24",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++++++++                +++++++++++++",
                    "start": 0,
                    "stop": 149
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGGGGGGGG________________ggggggggggggg",
                    "start": 0,
                    "stop": 149
                },
                "cdr3": {
                    "aa": "AACL#DWIKT",
                    "start": 113,
                    "stop": 141
                },
                "junction": {
                    "aa": "CAACL#DWIKTF",
                    "productive": false,
                    "start": 110,
                    "stop": 144
                }
            },
            "seg_stat": {
                "2": 9075,
                "3": 4
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTGCAAA",
            "top": 20
        },
        {
            "_average_read_length": [
                318.124755859375
            ],
            "_coverage": [
                0.9838907122612
            ],
            "_coverage_info": [
                "313 bp (98% of 318.1 bp)"
            ],
            "germline": "IGH",
            "id": "GCAGCCTAAAGGCTGAGGACACCCGACAGGGTATGGACGTCTGGGGCCAA",
            "name": "IGHV7-4-1*04 22/ACAG/17 IGHJ6*02",
            "reads": [
                9018
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 265,
                "5": "IGHV7-4-1*04",
                "5del": 22,
                "5end": 260,
                "N": 4,
                "_evalue": "1.512457e-95",
                "_evalue_left": "1.512457e-95",
                "_evalue_right": "1.802064e-127",
                "affectSigns": {
                    "seq": "  ----------------------------------                   --------------      -         -----      -      --------------------------------------                                                                                                                                                                ",
                    "start": 0,
                    "stop": 313
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh___________________HHHHHHHHHHHHHH______H_________HHHHH______H______HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH________________________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 313
                }
            },
            "seg_stat": {
                "2": 4212,
                "3": 4806
            },
            "sequence": "GGAGTCGGGGCCTGAGGTGAAGAAGCCTGGAGCCTCATTGAAGGTTTCCTGCAAGGCTTCTGGATACACCTTCACAAGCTATGCTATCAGCTGGGTATGACAGGCCCATGGACAAGGGCTTGAGGAAATGGGATGGATCAACACCAACACTGGGAACCTAACGTATGCCCAGGGCTTCACAGGACGGTTTGTCTTCTCCATGGACACCTCCGTCAGCATGGCATATCTTCATATCAGCAGCCTAAAGGCTGAGGACACCCGACAGGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 21
        },
        {
            "_average_read_length": [
                351.894622802734
            ],
            "_coverage": [
                1.00882470607758
            ],
            "_coverage_info": [
                "355 bp (100% of 351.9 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAAACTGGGCCCA",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                6881
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 306,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 248,
                "N": 57,
                "_evalue": "1.081312e-81",
                "_evalue_left": "1.081312e-81",
                "_evalue_right": "8.024297e-207",
                "affectSigns": {
                    "seq": "  -----------------------------------                                                                     -----------------------------------------------------------------------------------------                                                                                                                                                    ",
                    "start": 0,
                    "stop": 355
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh_____________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH____________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 355
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKAKLGPA#GMDV",
                    "start": 243,
                    "stop": 319
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKAKLGPA#GMDVW",
                    "productive": false,
                    "start": 240,
                    "stop": 322
                }
            },
            "seg_stat": {
                "3": 6881
            },
            "sequence": "GGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 22
        },
        {
            "_average_read_length": [
                316.652648925781
            ],
            "_coverage": [
                1.04530942440033
            ],
            "_coverage_info": [
                "331 bp (104% of 316.7 bp)"
            ],
            "germline": "IGH",
            "id": "CTGTGCGAGCCTACCCCGGGTATAGCAGTGCTGGTACGGGGGTGGTTCGA",
            "name": "IGHV3-11*01 3/CCTACCCC/0 IGHD6-19*01 0/GGGGG/5 IGHJ5*02",
            "reads": [
                5594
            ],
            "seg": {
                "3": "IGHJ5*02",
                "3del": 5,
                "3start": 283,
                "4": "IGHD6-19*01",
                "4delLeft": 0,
                "4delRight": 0,
                "4end": 277,
                "4start": 258,
                "5": "IGHV3-11*01",
                "5del": 3,
                "5end": 249,
                "N1": 8,
                "N2": 5,
                "_evalue": "1.483841e-84",
                "_evalue_left": "1.483841e-84",
                "_evalue_right": "1.065158e-204",
                "affectSigns": {
                    "seq": "  ----------------------------------                        -                    ---------------------------------------------------------------------------------------                                     +                                                                                                                 ",
                    "start": 0,
                    "stop": 331
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh________________________V____________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_____________________________________h_________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 331
                },
                "cdr3": {
                    "aa": "ASLPRV*QCWYG#WFDP",
                    "start": 246,
                    "stop": 295
                },
                "junction": {
                    "aa": "CASLPRV*QCWYG#WFDPW",
                    "productive": false,
                    "start": 243,
                    "stop": 298
                }
            },
            "seg_stat": {
                "3": 5594
            },
            "sequence": "GGAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTGACTACTACATGAGCTGGATCCGCCAGGCTCCAGGGAAGGGGCTGGAGTGGGTTTCATACATTAGTAGTAGTGGTAGTACCATATACTACGCAGACTCTGTGAAGGGCCGATTCACCATCTCCAGGGACAACGCCAAGAACTCACTGTATCTGCAAATGAACAGCCTGAGAGCCGAGGACACGGCCGTGTATTACTGTGCGAGCCTACCCCGGGTATAGCAGTGCTGGTACGGGGGTGGTTCGACCCCTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 23
        },
        {
            "_average_read_length": [
                246.563888549805
            ],
            "_coverage": [
                1.01393604278564
            ],
            "_coverage_info": [
                "250 bp (101% of 246.6 bp)"
            ],
            "germline": "TRG",
            "id": "TTACTGTGCCACCTGGGACGGGCCGGAGGATACCACTGGTTGGTTCAAGA",
            "name": "TRGV2*01 0/CCGGAGG/0 TRGJP1*01",
            "reads": [
                5517
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 0,
                "3start": 186,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 7,
                "_evalue": "2.510810e-119",
                "_evalue_left": "2.510810e-119",
                "_evalue_right": "6.746545e-195",
                "affectSigns": {
                    "seq": "    --------------------------------------------------                 ------------------------------------------------------------------------------------------ --                                                                            ",
                    "start": 0,
                    "stop": 250
                },
                "affectValues": {
                    "seq": "____gggggggggggggggggggggggggggggggggggggggggggggggggg_________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 250
                },
                "cdr3": {
                    "aa": "ATWDGPEDTTGWFKI",
                    "start": 165,
                    "stop": 209
                },
                "junction": {
                    "aa": "CATWDGPEDTTGWFKIF",
                    "productive": true,
                    "start": 162,
                    "stop": 212
                }
            },
            "seg_stat": {
                "2": 3519,
                "3": 1998
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCCGGAGGATACCACTGGTTGGTTCAAGATATTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 24
        },
        {
            "_average_read_length": [
                214.721984863281
            ],
            "_coverage": [
                1.00129473209381
            ],
            "_coverage_info": [
                "215 bp (100% of 214.7 bp)"
            ],
            "germline": "TRG",
            "id": "ATTACTGTGCCACCCCTCCTGCTCATTCCACGAGAGAAAACTCTTTGGCA",
            "name": "TRGV2*02 6/CCTCCTGCTCATTCCACGAG/8 TRGJ1*02",
            "reads": [
                5327
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 8,
                "3start": 187,
                "5": "TRGV2*02",
                "5del": 6,
                "5end": 166,
                "N": 20,
                "_evalue": "1.872196e-24",
                "_evalue_left": "1.872196e-24",
                "_evalue_right": "4.244054e-169",
                "affectSigns": {
                    "seq": "---------------                                 --------------------------------------------------------------------------------- --                                                                         ",
                    "start": 0,
                    "stop": 215
                },
                "affectValues": {
                    "seq": "ggggggggggggggg_________________________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG_________________________________________________________________________",
                    "start": 0,
                    "stop": 215
                },
                "cdr3": {
                    "aa": "ATPPAHST#RKL",
                    "start": 162,
                    "stop": 196
                },
                "junction": {
                    "aa": "CATPPAHST#RKLF",
                    "productive": false,
                    "start": 159,
                    "stop": 199
                }
            },
            "seg_stat": {
                "2": 13,
                "3": 5314
            },
            "sequence": "AGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCCCTCCTGCTCATTCCACGAGAGAAAACTCTTTGGCAGTGGAACAACAC",
            "top": 25
        },
        {
            "_average_read_length": [
                252.069396972656
            ],
            "_coverage": [
                1.08303511142731
            ],
            "_coverage_info": [
                "273 bp (108% of 252.1 bp)"
            ],
            "germline": "IGH",
            "id": "CACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGCCAAGG",
            "name": "IGHV3-9*01 7/CCCGGA/17 IGHJ6*02",
            "reads": [
                4986
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 226,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 219,
                "N": 6,
                "_evalue": "7.182992e-61",
                "_evalue_left": "7.182992e-61",
                "_evalue_right": "1.208959e-212",
                "affectSigns": {
                    "seq": "  --------------------         ----                 --------------------------------------------------------------------------------------                                                                                                                           ",
                    "start": 0,
                    "stop": 273
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhh_________hhhh_________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH___________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 273
                },
                "cdr3": {
                    "aa": "APGGMDV",
                    "start": 218,
                    "stop": 238
                },
                "junction": {
                    "aa": "CAPGGMDVW",
                    "productive": true,
                    "start": 215,
                    "stop": 241
                }
            },
            "seg_stat": {
                "2": 1630,
                "3": 3356
            },
            "sequence": "CTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 26
        },
        {
            "_average_read_length": [
                174.073104858398
            ],
            "_coverage": [
                1.00532472133636
            ],
            "_coverage_info": [
                "175 bp (100% of 174.1 bp)"
            ],
            "germline": "TRG",
            "id": "AAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTTG",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                4965
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 115,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 114,
                "N": 0,
                "_evalue": "1.110495e-85",
                "_evalue_left": "2.766642e-186",
                "_evalue_right": "1.110495e-85",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++            +++++++++++        +++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 175
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG____________ggggggggggg________ggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 175
                },
                "cdr3": {
                    "aa": "A#TGWFKI",
                    "start": 112,
                    "stop": 133
                },
                "junction": {
                    "aa": "CA#TGWFKIF",
                    "productive": false,
                    "start": 109,
                    "stop": 136
                }
            },
            "seg_stat": {
                "2": 4962,
                "3": 3
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 27
        },
        {
            "_average_read_length": [
                318.461578369141
            ],
            "_coverage": [
                1.07391285896301
            ],
            "_coverage_info": [
                "342 bp (107% of 318.5 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCAG",
            "name": "IGHV3-13*05 1/GAGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                4918
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 293,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 237,
                "N": 55,
                "_evalue": "4.237222e-82",
                "_evalue_left": "4.237222e-82",
                "_evalue_right": "6.605717e-209",
                "affectSigns": {
                    "seq": "  -----------------------------------                                                                   -----------------------------------------------------------------------------------------                                                                                                                                         ",
                    "start": 0,
                    "stop": 342
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh___________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 342
                },
                "cdr3": {
                    "aa": "ARGGGLPPPL*PVKSKTGPSGGMDV",
                    "start": 232,
                    "stop": 306
                },
                "junction": {
                    "aa": "CARGGGLPPPL*PVKSKTGPSGGMDVW",
                    "productive": false,
                    "start": 229,
                    "stop": 309
                }
            },
            "seg_stat": {
                "3": 4918
            },
            "sequence": "AGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 28
        },
        {
            "_average_read_length": [
                233.197357177734
            ],
            "_coverage": [
                0.943406939506531
            ],
            "_coverage_info": [
                "220 bp (94% of 233.2 bp)"
            ],
            "germline": "TRG",
            "id": "GCCACCTGGGACGGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAAA",
            "name": "TRGV2*01 0/CCTACTCCAAGGCCCACTCGG/0 TRGJ1*02",
            "reads": [
                4469
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 0,
                "3start": 184,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 162,
                "N": 21,
                "_evalue": "3.038774e-30",
                "_evalue_left": "3.038774e-30",
                "_evalue_right": "1.532538e-195",
                "affectSigns": {
                    "seq": "---------------        ---                               ------------------------------------------------------------------------------------------ --                                                            ",
                    "start": 0,
                    "stop": 220
                },
                "affectValues": {
                    "seq": "ggggggggggggggg________ggg_______________________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________",
                    "start": 0,
                    "stop": 220
                },
                "cdr3": {
                    "aa": "ATWDGPTPRPTR#IIRKL",
                    "start": 149,
                    "stop": 201
                },
                "junction": {
                    "aa": "CATWDGPTPRPTR#IIRKLF",
                    "productive": false,
                    "start": 146,
                    "stop": 204
                }
            },
            "seg_stat": {
                "2": 5,
                "3": 4464
            },
            "sequence": "TCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAAACTCTTTGGCAGTGGAACAACAC",
            "top": 29
        },
        {
            "_average_read_length": [
                241.570205688477
            ],
            "_coverage": [
                0.695450007915497
            ],
            "_coverage_info": [
                "168 bp (69% of 241.6 bp)"
            ],
            "germline": "TRG",
            "id": "TATTACTGTGCCACCTGGGACAGGCGGAGTAGTGATTGGATCAAGACGTT",
            "name": "TRGV3*01 0/CGG/2 TRGJP2*01",
            "reads": [
                4444
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 2,
                "3start": 106,
                "5": "TRGV3*01",
                "5del": 0,
                "5end": 102,
                "N": 3,
                "_evalue": "3.912734e-117",
                "_evalue_left": "3.912734e-117",
                "_evalue_right": "5.571064e-236",
                "affectSigns": {
                    "seq": "    ------------------------------------------------             ------------------------------------------------------------------------------------------ - ",
                    "start": 0,
                    "stop": 168
                },
                "affectValues": {
                    "seq": "____gggggggggggggggggggggggggggggggggggggggggggggggg_____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?G_",
                    "start": 0,
                    "stop": 168
                },
                "cdr3": {
                    "aa": "ATWDRRSSDWIKT",
                    "start": 89,
                    "stop": 127
                },
                "junction": {
                    "aa": "CATWDRRSSDWIKTF",
                    "productive": true,
                    "start": 86,
                    "stop": 130
                }
            },
            "seg_stat": {
                "2": 2815,
                "3": 1629
            },
            "sequence": "AAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACAGGCGGAGTAGTGATTGGATCAAGACGTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 30
        },
        {
            "_average_read_length": [
                246.470092773438
            ],
            "_coverage": [
                1.00620722770691
            ],
            "_coverage_info": [
                "248 bp (100% of 246.5 bp)"
            ],
            "germline": "TRG",
            "id": "TTACTGTGCCACCTGGGACGGGTCCCCCTGCCACTGGTTGGTTCAAGATA",
            "name": "TRGV2*01 0/TCCCCCTG/3 TRGJP1*01",
            "reads": [
                4263
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 3,
                "3start": 187,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 8,
                "_evalue": "1.208313e-110",
                "_evalue_left": "1.208313e-110",
                "_evalue_right": "1.402408e-194",
                "affectSigns": {
                    "seq": "    -----------------------------------------------                  ------------------------------------------------------------------------------------------ --                                                                            ",
                    "start": 0,
                    "stop": 248
                },
                "affectValues": {
                    "seq": "____ggggggggggggggggggggggggggggggggggggggggggggggg__________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 248
                },
                "cdr3": {
                    "aa": "ATWDGSPC#TGWFKI",
                    "start": 165,
                    "stop": 207
                },
                "junction": {
                    "aa": "CATWDGSPC#TGWFKIF",
                    "productive": false,
                    "start": 162,
                    "stop": 210
                }
            },
            "seg_stat": {
                "2": 2100,
                "3": 2163
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGTCCCCCTGCCACTGGTTGGTTCAAGATATTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 31
        },
        {
            "_average_read_length": [
                233.367172241211
            ],
            "_coverage": [
                0.724180698394775
            ],
            "_coverage_info": [
                "169 bp (72% of 233.4 bp)"
            ],
            "germline": "TRG",
            "id": "TGCCACCTGGGACGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAAC",
            "name": "TRGV2*02 0/GGCCTACTCCAAGGCCCACTCGG/0 TRGJ1*02",
            "reads": [
                4197
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 0,
                "3start": 150,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 126,
                "N": 23,
                "_evalue": "5.638418e-11",
                "_evalue_left": "1.381182e-205",
                "_evalue_right": "5.638418e-11",
                "affectSigns": {
                    "seq": "                           ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                               +++++++++",
                    "start": 0,
                    "stop": 169
                },
                "affectValues": {
                    "seq": "___________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_______________________________ggggggggg",
                    "start": 0,
                    "stop": 169
                },
                "cdr3": {
                    "aa": "ATWDGLLQGPLGYYKKL",
                    "start": 116,
                    "stop": 166
                },
                "junction": {
                    "aa": "CATWDGLLQGPLGYYKKLF",
                    "productive": true,
                    "start": 113,
                    "stop": 169
                }
            },
            "seg_stat": {
                "2": 4197
            },
            "sequence": "TGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAACTCTTT",
            "top": 32
        },
        {
            "_average_read_length": [
                221.943054199219
            ],
            "_coverage": [
                1.00926792621613
            ],
            "_coverage_info": [
                "224 bp (100% of 221.9 bp)"
            ],
            "germline": "TRG",
            "id": "CTGTGCCACCTGGGACGGTCTACCCCACGATTATATAAGAAACTCTTTGG",
            "name": "TRGV2*02 0/GGTCTACCCCACG/2 TRGJ1*01",
            "reads": [
                4092
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 2,
                "3start": 189,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 175,
                "N": 13,
                "_evalue": "2.105542e-41",
                "_evalue_left": "5.635239e-190",
                "_evalue_right": "2.105542e-41",
                "affectSigns": {
                    "seq": "                                                                            ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                         +++++++++++++++++++++",
                    "start": 0,
                    "stop": 224
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_________________________ggggggggggggggggggggg",
                    "start": 0,
                    "stop": 224
                },
                "cdr3": {
                    "aa": "ATWDGLPH#LYKKL",
                    "start": 165,
                    "stop": 205
                },
                "junction": {
                    "aa": "CATWDGLPH#LYKKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 208
                }
            },
            "seg_stat": {
                "2": 3876,
                "3": 216
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGTCTACCCCACGATTATATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 33
        },
        {
            "_average_read_length": [
                302.035064697266
            ],
            "_coverage": [
                1.04954695701599
            ],
            "_coverage_info": [
                "317 bp (104% of 302.0 bp)"
            ],
            "germline": "IGH",
            "id": "GTGTATTACTGTGCGAGAGACGGGGCCTGGGACTGGTTCGACCCCTGGGG",
            "name": "IGHV3-21*01 0/CGGGGCCTGGG/3 IGHJ5*02",
            "reads": [
                3964
            ],
            "seg": {
                "3": "IGHJ5*02",
                "3del": 3,
                "3start": 267,
                "5": "IGHV3-21*01",
                "5del": 0,
                "5end": 255,
                "N": 11,
                "_evalue": "2.092231e-99",
                "_evalue_left": "1.890866e-216",
                "_evalue_right": "2.092231e-99",
                "affectSigns": {
                    "seq": "                                                                                                             -------                                      ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                       ++++++++++++++++++++++++++++++++++++  ",
                    "start": 0,
                    "stop": 317
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________________________________________hhhhhhh______________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_______________________hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 317
                },
                "cdr3": {
                    "aa": "ARDGAWDWFDP",
                    "start": 249,
                    "stop": 281
                },
                "junction": {
                    "aa": "CARDGAWDWFDPW",
                    "productive": true,
                    "start": 246,
                    "stop": 284
                }
            },
            "seg_stat": {
                "2": 568,
                "3": 3396
            },
            "sequence": "GGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTATAGCATGAACTGGGTCCGCCAGGCTCCAGGGAAGGGGCTGGAGTGGGTCTCATCCATTAGTAGTAGTAGTAGTAGTTACATATACTACGCAGACTCAGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCACTGTATCTGCAAATGAACAGCCTGAGAGCCGAGGACACGGCTGTGTATTACTGTGCGAGAGACGGGGCCTGGGACTGGTTCGACCCCTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 34
        },
        {
            "_average_read_length": [
                320.665283203125
            ],
            "_coverage": [
                0.988569736480713
            ],
            "_coverage_info": [
                "317 bp (98% of 320.7 bp)"
            ],
            "germline": "IGH",
            "id": "GCCTTGTATTACTGTGCACCCCGGGTATCGGGTGGGGCCAAGGGACCCTG",
            "name": "IGHV3-9*01 7/CCCCGGGTATCGGG/14 IGHJ4*03",
            "reads": [
                3376
            ],
            "seg": {
                "3": "IGHJ4*03",
                "3del": 14,
                "3start": 284,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 269,
                "N": 14,
                "_evalue": "6.539617e-53",
                "_evalue_left": "6.539617e-53",
                "_evalue_right": "1.341107e-201",
                "affectSigns": {
                    "seq": "---------------------                         --------------------------------------------------------------------------------------                                                                                                                                                                             ",
                    "start": 0,
                    "stop": 317
                },
                "affectValues": {
                    "seq": "hhhhhhhhhhhhhhhhhhhhh_________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_____________________________________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 317
                },
                "cdr3": {
                    "aa": "APRVS#",
                    "start": 268,
                    "stop": 284
                },
                "junction": {
                    "aa": "CAPRVS#W",
                    "productive": false,
                    "start": 265,
                    "stop": 287
                }
            },
            "seg_stat": {
                "2": 954,
                "3": 2422
            },
            "sequence": "GGGGGAGGCTTGGTACAGCCTGGCAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCACCCCGGGTATCGGGTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCA",
            "top": 35
        },
        {
            "_average_read_length": [
                340.414367675781
            ],
            "_coverage": [
                0.951781213283539
            ],
            "_coverage_info": [
                "324 bp (95% of 340.4 bp)"
            ],
            "germline": "IGH",
            "id": "CCTTGTATTACTGTGCACCCGGAGGTATGGACGCTGGGGCCAAGGGACCC",
            "name": "IGHV3-9*01 7/CCCGGA/17 IGHJ6*02",
            "reads": [
                3299
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 277,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 270,
                "N": 6,
                "_evalue": "1.857616e-57",
                "_evalue_left": "4.145197e-201",
                "_evalue_right": "1.857616e-57",
                "affectSigns": {
                    "seq": "                                                                                                                                                                              ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                           +++++++++++++++++++++++  ",
                    "start": 0,
                    "stop": 324
                },
                "affectValues": {
                    "seq": "______________________________________________________________________________________________________________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH___________________________hhhhhhhhhhhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 324
                },
                "cdr3": {
                    "aa": "APG#YGR",
                    "start": 269,
                    "stop": 288
                },
                "junction": {
                    "aa": "CAPG#YGRW",
                    "productive": false,
                    "start": 266,
                    "stop": 291
                }
            },
            "seg_stat": {
                "2": 3295,
                "3": 4
            },
            "sequence": "GGGGGGAGGCTTGGTACAGCCTGGCAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 36
        },
        {
            "_average_read_length": [
                240.168334960938
            ],
            "_coverage": [
                1.01595413684845
            ],
            "_coverage_info": [
                "244 bp (101% of 240.2 bp)"
            ],
            "germline": "TRG",
            "id": "TATTACTGTGCCACCTGGGACGGGCCGAGTAGTGATTGGATCAAGACGTT",
            "name": "TRGV2*01 0/CCG/2 TRGJP2*01",
            "reads": [
                3184
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 2,
                "3start": 182,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 3,
                "_evalue": "3.912734e-117",
                "_evalue_left": "3.912734e-117",
                "_evalue_right": "3.393420e-196",
                "affectSigns": {
                    "seq": "    ------------------------------------------------             ------------------------------------------------------------------------------------------ --                                                                            ",
                    "start": 0,
                    "stop": 244
                },
                "affectValues": {
                    "seq": "____gggggggggggggggggggggggggggggggggggggggggggggggg_____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 244
                },
                "cdr3": {
                    "aa": "ATWDGPSSDWIKT",
                    "start": 165,
                    "stop": 203
                },
                "junction": {
                    "aa": "CATWDGPSSDWIKTF",
                    "productive": true,
                    "start": 162,
                    "stop": 206
                }
            },
            "seg_stat": {
                "2": 1992,
                "3": 1192
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCCGAGTAGTGATTGGATCAAGACGTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 37
        },
        {
            "_average_read_length": [
                232.128936767578
            ],
            "_coverage": [
                1.01236844062805
            ],
            "_coverage_info": [
                "235 bp (101% of 232.1 bp)"
            ],
            "germline": "TRG",
            "id": "GCCACCTGGGACGGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAAC",
            "name": "TRGV2*01 0/CCTACTCCAAGGCCCACTCGG/0 TRGJ1*02",
            "reads": [
                3149
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 0,
                "3start": 200,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 21,
                "_evalue": "2.556104e-49",
                "_evalue_left": "1.132069e-190",
                "_evalue_right": "2.556104e-49",
                "affectSigns": {
                    "seq": "                                                                            ++ ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                               +++++++++++++++++++++++++",
                    "start": 0,
                    "stop": 235
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_______________________________ggggggggggggggggggggggggg",
                    "start": 0,
                    "stop": 235
                },
                "cdr3": {
                    "aa": "ATWDGPTPRPTR#YYKKL",
                    "start": 165,
                    "stop": 216
                },
                "junction": {
                    "aa": "CATWDGPTPRPTR#YYKKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 219
                }
            },
            "seg_stat": {
                "2": 1309,
                "3": 1840
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCCTACTCCAAGGCCCACTCGGTTATTATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 38
        },
        {
            "_average_read_length": [
                304.236450195312
            ],
            "_coverage": [
                1.11097800731659
            ],
            "_coverage_info": [
                "338 bp (111% of 304.2 bp)"
            ],
            "germline": "IGH",
            "id": "GGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGC",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                3007
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 290,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 234,
                "N": 55,
                "_evalue": "1.347723e-55",
                "_evalue_left": "1.347723e-55",
                "_evalue_right": "4.317147e-208",
                "affectSigns": {
                    "seq": "  --------------------------                                                                           -----------------------------------------------------------------------------------------                                                                                                                                      ",
                    "start": 0,
                    "stop": 338
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhh___________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH______________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 338
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKQTGPS#RYGV",
                    "start": 229,
                    "stop": 302
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKQTGPS#RYGVW",
                    "productive": false,
                    "start": 226,
                    "stop": 305
                }
            },
            "seg_stat": {
                "2": 75,
                "3": 2932
            },
            "sequence": "CTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGGTATGGCGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 39
        },
        {
            "_average_read_length": [
                214.116012573242
            ],
            "_coverage": [
                1.01346921920776
            ],
            "_coverage_info": [
                "217 bp (101% of 214.1 bp)"
            ],
            "germline": "TRG",
            "id": "CTGTGCCACCTGGGACGGGCAGGGTTATAAGAAAACTCTTTGGCAGTGGA",
            "name": "TRGV2*01 0/CAGGG/3 TRGJ1*02",
            "reads": [
                2922
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 3,
                "3start": 184,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 5,
                "_evalue": "4.606823e-27",
                "_evalue_left": "4.606823e-27",
                "_evalue_right": "4.980000e-193",
                "affectSigns": {
                    "seq": "---------------                       ------------------------------------------------------------------------------------------ --                                                                            ",
                    "start": 0,
                    "stop": 217
                },
                "affectValues": {
                    "seq": "ggggggggggggggg_______________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 217
                },
                "cdr3": {
                    "aa": "ATWDGQG#IRKL",
                    "start": 165,
                    "stop": 198
                },
                "junction": {
                    "aa": "CATWDGQG#IRKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 201
                }
            },
            "seg_stat": {
                "2": 4,
                "3": 2918
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCAGGGTTATAAGAAAACTCTTTGGCAGTGGAACAACAC",
            "top": 40
        },
        {
            "_average_read_length": [
                373.537841796875
            ],
            "_coverage": [
                0.859350681304932
            ],
            "_coverage_info": [
                "321 bp (85% of 373.5 bp)"
            ],
            "germline": "IGH",
            "id": "GGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGG",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGG/2 IGHJ4*03",
            "reads": [
                2880
            ],
            "seg": {
                "3": "IGHJ4*03",
                "3del": 2,
                "3start": 274,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 215,
                "N": 58,
                "_evalue": "6.469530e-67",
                "_evalue_left": "1.222115e-211",
                "_evalue_right": "6.469530e-67",
                "affectSigns": {
                    "seq": "                                                                                                                   +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                                                                         ++++++++++++++++++++++++++++++  ",
                    "start": 0,
                    "stop": 321
                },
                "affectValues": {
                    "seq": "___________________________________________________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_________________________________________________________________________hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 321
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKQTGPSGG#LDV",
                    "start": 210,
                    "stop": 285
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKQTGPSGG#LDVW",
                    "productive": false,
                    "start": 207,
                    "stop": 288
                }
            },
            "seg_stat": {
                "2": 2334,
                "3": 546
            },
            "sequence": "GGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGGTACTGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 41
        },
        {
            "_average_read_length": [
                206.415588378906
            ],
            "_coverage": [
                0.993142068386078
            ],
            "_coverage_info": [
                "205 bp (99% of 206.4 bp)"
            ],
            "germline": "TRG",
            "id": "GGTCTATTACTGTGCCACCTTCTGACATAAGAAAACTCTTTGGCAGTGGA",
            "name": "TRGV3*01 2//6 TRGJ1*02",
            "reads": [
                2873
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 6,
                "3start": 178,
                "5": "TRGV3*01",
                "5del": 2,
                "5end": 177,
                "N": 0,
                "_evalue": "6.001938e-20",
                "_evalue_left": "6.001938e-20",
                "_evalue_right": "5.113203e-171",
                "affectSigns": {
                    "seq": "------------                      ---------------------------------------------------------------------------------- -                                                                             ",
                    "start": 0,
                    "stop": 205
                },
                "affectValues": {
                    "seq": "gggggggggggg______________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?G_____________________________________________________________________________",
                    "start": 0,
                    "stop": 205
                },
                "cdr3": {
                    "aa": "ATF*H#RKL",
                    "start": 165,
                    "stop": 189
                },
                "junction": {
                    "aa": "CATF*H#RKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 192
                }
            },
            "seg_stat": {
                "2": 11,
                "3": 2862
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTTCTGACATAAGAAAACTCTTTGGCAGTGGAACAA",
            "top": 42
        },
        {
            "_average_read_length": [
                333.545043945312
            ],
            "_coverage": [
                1.04333734512329
            ],
            "_coverage_info": [
                "348 bp (104% of 333.5 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAACTGGGCCCA",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAACTGGGCCCAGCGG/16 IGHJ6*02",
            "reads": [
                2809
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 16,
                "3start": 299,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 242,
                "N": 56,
                "_evalue": "6.790562e-82",
                "_evalue_left": "6.790562e-82",
                "_evalue_right": "6.254307e-208",
                "affectSigns": {
                    "seq": "  -----------------------------------                                                                    -----------------------------------------------------------------------------------------                                                                                                                                              ",
                    "start": 0,
                    "stop": 348
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh____________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH______________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 348
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKANWAQR#GMDV",
                    "start": 237,
                    "stop": 312
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKANWAQR#GMDVW",
                    "productive": false,
                    "start": 234,
                    "stop": 315
                }
            },
            "seg_stat": {
                "3": 2809
            },
            "sequence": "CCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAAGCAAACTGGGCCCAGCGGCGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 43
        },
        {
            "_average_read_length": [
                332.561889648438
            ],
            "_coverage": [
                0.974254727363586
            ],
            "_coverage_info": [
                "324 bp (97% of 332.6 bp)"
            ],
            "germline": "IGH",
            "id": "CACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAGG",
            "name": "IGHV3-9*01 7/CCCGGA/17 IGHJ6*02",
            "reads": [
                2739
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 277,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 270,
                "N": 6,
                "_evalue": "1.857616e-57",
                "_evalue_left": "9.076066e-203",
                "_evalue_right": "1.857616e-57",
                "affectSigns": {
                    "seq": "                                                                                                                                                                              ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                 ++++++++   +       ++++++++++++++  ",
                    "start": 0,
                    "stop": 324
                },
                "affectValues": {
                    "seq": "______________________________________________________________________________________________________________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_________________hhhhhhhh___h_______hhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 324
                },
                "cdr3": {
                    "aa": "APGGMDV",
                    "start": 269,
                    "stop": 289
                },
                "junction": {
                    "aa": "CAPGGMDVW",
                    "productive": true,
                    "start": 266,
                    "stop": 292
                }
            },
            "seg_stat": {
                "2": 2477,
                "3": 262
            },
            "sequence": "GGGGGGAGGCTTGGTACAGCCTGGCAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 44
        },
        {
            "_average_read_length": [
                337.445434570312
            ],
            "_coverage": [
                0.886069178581238
            ],
            "_coverage_info": [
                "299 bp (88% of 337.4 bp)"
            ],
            "germline": "IGH",
            "id": "ACACGGCCTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAAG",
            "name": "IGHV3-9*01 7/CCCGGA/17 IGHJ6*02",
            "reads": [
                2712
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 275,
                "5": "IGHV3-9*01",
                "5del": 7,
                "5end": 268,
                "N": 6,
                "_evalue": "1.021858e-28",
                "_evalue_left": "4.091913e-171",
                "_evalue_right": "1.021858e-28",
                "affectSigns": {
                    "seq": "                                                                                                                                                                             +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++         + +++                 ++++++++++++",
                    "start": 0,
                    "stop": 299
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________________________________________________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_________H_HHH_________________hhhhhhhhhhhh",
                    "start": 0,
                    "stop": 299
                },
                "cdr3": {
                    "aa": "APGGMDV",
                    "start": 267,
                    "stop": 287
                },
                "junction": {
                    "aa": "CAPGGMDVW",
                    "productive": true,
                    "start": 264,
                    "stop": 290
                }
            },
            "seg_stat": {
                "2": 2675,
                "3": 37
            },
            "sequence": "GGGGGAGGCTTGGTACAGCCTGGCAGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTGATGATTATGCCATGCACTGGGTCCGGCAAGCTCCAGGGAAGGGCCTGGAGTGGGTCTCAGGTATTAGTTGGAATAGTGGTAGCATAGGCTATGCGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCCCTGTATCTGCAAATGAACAGTCTGAGAGCTGAGGACACGGCCTGTATTACTGTGCACCCGGAGGTATGGACGTCTGGGGCCAAGGG",
            "top": 45
        },
        {
            "_average_read_length": [
                299.817321777344
            ],
            "_coverage": [
                0.920560538768768
            ],
            "_coverage_info": [
                "276 bp (92% of 299.8 bp)"
            ],
            "germline": "IGH",
            "id": "GGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGC",
            "name": "IGHV3-13*05 1/GAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCG/17 IGHJ6*02",
            "reads": [
                2239
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 17,
                "3start": 228,
                "5": "IGHV3-13*05",
                "5del": 1,
                "5end": 170,
                "N": 57,
                "_evalue": "7.379237e-79",
                "_evalue_left": "7.379237e-79",
                "_evalue_right": "1.714027e-221",
                "affectSigns": {
                    "seq": "  ----------------------------------                                                                     -----------------------------------------------------------------------------------------                                                                      ",
                    "start": 0,
                    "stop": 276
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh_____________________________________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH______________________________________________________________________",
                    "start": 0,
                    "stop": 276
                },
                "cdr3": {
                    "aa": "ARGGGPPSTPLTSEKQTGPSG#GMDV",
                    "start": 165,
                    "stop": 240
                },
                "junction": {
                    "aa": "CARGGGPPSTPLTSEKQTGPSG#GMDVW",
                    "productive": false,
                    "start": 162,
                    "stop": 243
                }
            },
            "seg_stat": {
                "2": 57,
                "3": 2182
            },
            "sequence": "CAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACCCATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGGAGGGGGGCCTCCCTCCACCCCTCTAACCAGTGAAAAGCAAACTGGGCCCAGCGGCGGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 46
        },
        {
            "_average_read_length": [
                173.56413269043
            ],
            "_coverage": [
                0.910326302051544
            ],
            "_coverage_info": [
                "158 bp (91% of 173.6 bp)"
            ],
            "germline": "TRG",
            "id": "AGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTGCT",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                2191
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 116,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 115,
                "N": 0,
                "_evalue": "2.777383e-51",
                "_evalue_left": "1.056383e-163",
                "_evalue_right": "2.777383e-51",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++            ++++++++++        ++++++++++++++",
                    "start": 0,
                    "stop": 158
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGG____________gggggggggg________gggggggggggggg",
                    "start": 0,
                    "stop": 158
                }
            },
            "seg_stat": {
                "2": 2191
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTGCTGAAGGGACTAAGCTCATAG",
            "top": 47
        },
        {
            "_average_read_length": [
                290.46044921875
            ],
            "_coverage": [
                1.08792781829834
            ],
            "_coverage_info": [
                "316 bp (108% of 290.5 bp)"
            ],
            "germline": "IGH",
            "id": "CGTATATTACTGTGCTCAACTGGACCCACGTGGGGGCTGGTTCGACCCCT",
            "name": "IGHV3-23*01 6/T/4 IGHD1-1*01 5/CCCACGTGGGGG/4 IGHJ5*02",
            "reads": [
                2098
            ],
            "seg": {
                "3": "IGHJ5*02",
                "3del": 4,
                "3start": 267,
                "4": "IGHD1-1*01",
                "4delLeft": 4,
                "4delRight": 5,
                "4end": 254,
                "4start": 247,
                "5": "IGHV3-23*01",
                "5del": 6,
                "5end": 245,
                "N1": 1,
                "N2": 12,
                "_evalue": "1.278120e-91",
                "_evalue_left": "1.399671e-198",
                "_evalue_right": "1.278120e-91",
                "affectSigns": {
                    "seq": "                                                                                                                 ++                                   ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                                 +++++++++++++++++++++++++++++++++++  ",
                    "start": 0,
                    "stop": 316
                },
                "affectValues": {
                    "seq": "_________________________________________________________________________________________________________________VV___________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_________________________________hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh__",
                    "start": 0,
                    "stop": 316
                },
                "cdr3": {
                    "aa": "AQLDPRGGWFDP",
                    "start": 245,
                    "stop": 280
                },
                "junction": {
                    "aa": "CAQLDPRGGWFDPW",
                    "productive": true,
                    "start": 242,
                    "stop": 283
                }
            },
            "seg_stat": {
                "2": 68,
                "3": 2030
            },
            "sequence": "GGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTAGCAGCTATGCCATGAGCTGGGTCCGCCAGGCTCCAGGGAAGGGGCTGGAGTGGGTCTCAGCTATTAGTGGTAGTGGTGGTAGCACATACTACGCAGACTCCGTGAAGGGCCGGTTCACCATCTCCAGAGACAATTCCAAGAACACGCTGTATCTGCAAATGAACAGCCTGAGAGCCGAGGACACGGCCGTATATTACTGTGCTCAACTGGACCCACGTGGGGGCTGGTTCGACCCCTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 48
        },
        {
            "_average_read_length": [
                221.896530151367
            ],
            "_coverage": [
                0.991453111171722
            ],
            "_coverage_info": [
                "220 bp (99% of 221.9 bp)"
            ],
            "germline": "TRG",
            "id": "CTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTGG",
            "name": "TRGV2*02 0/GGTCTACCCCCACG/2 TRGJ1*01",
            "reads": [
                1933
            ],
            "seg": {
                "3": "TRGJ1*01",
                "3del": 2,
                "3start": 190,
                "5": "TRGV2*02",
                "5del": 0,
                "5end": 175,
                "N": 14,
                "_evalue": "2.330592e-08",
                "_evalue_left": "1.112976e-189",
                "_evalue_right": "2.330592e-08",
                "affectSigns": {
                    "seq": "                                                                            ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                          ++++        ++++",
                    "start": 0,
                    "stop": 220
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG__________________________gggg________gggg",
                    "start": 0,
                    "stop": 220
                }
            },
            "seg_stat": {
                "2": 1833,
                "3": 100
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGTCTACCCCCACGATTATATAAGAAACTCTTGGCAGTGGAACA",
            "top": 49
        },
        {
            "_average_read_length": [
                181.983276367188
            ],
            "_coverage": [
                1.00558686256409
            ],
            "_coverage_info": [
                "183 bp (100% of 182.0 bp)"
            ],
            "germline": "TRG",
            "id": "GCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTG",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                1914
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 125,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 124,
                "N": 0,
                "_evalue": "2.057941e-75",
                "_evalue_left": "1.106911e-199",
                "_evalue_right": "2.057941e-75",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                ++++++++         +++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 183
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________________gggggggg_________ggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 183
                },
                "cdr3": {
                    "aa": "AACLGIGSRR",
                    "start": 112,
                    "stop": 141
                },
                "junction": {
                    "aa": "CAACLGIGSRRF",
                    "productive": true,
                    "start": 109,
                    "stop": 144
                }
            },
            "seg_stat": {
                "2": 1914
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTGCAAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 50
        },
        {
            "_average_read_length": [
                177.334045410156
            ],
            "_coverage": [
                0.654132723808289
            ],
            "_coverage_info": [
                "116 bp (65% of 177.3 bp)"
            ],
            "germline": "TRG",
            "id": "AGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTTCAAGATATTTT",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                1853
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 55,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 54,
                "N": 0,
                "_evalue": "3.983252e-61",
                "_evalue_left": "1.488269e-70",
                "_evalue_right": "3.983252e-61",
                "affectSigns": {
                    "seq": "   +++++++++++++++++++++++++++++        +++            +         ++        +++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 116
                },
                "affectValues": {
                    "seq": "___GGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGG____________g_________gg________ggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 116
                },
                "cdr3": {
                    "aa": "A#LVGFKI",
                    "start": 52,
                    "stop": 74
                },
                "junction": {
                    "aa": "CA#LVGFKIF",
                    "productive": false,
                    "start": 49,
                    "stop": 77
                }
            },
            "seg_stat": {
                "2": 1853
            },
            "sequence": "CCTTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCGGACTGGTTGGTTTCAAGATATTTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 51
        },
        {
            "_average_read_length": [
                212.999450683594
            ],
            "_coverage": [
                1.00000262260437
            ],
            "_coverage_info": [
                "213 bp (100% of 213.0 bp)"
            ],
            "germline": "TRG",
            "id": "ACTGTGCCACCTGGGACAGGCGGGGTATAAGAAAACTCTTTGGCAGTGGA",
            "name": "TRGV3*01 0/CGGGG/4 TRGJ1*02",
            "reads": [
                1807
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 4,
                "3start": 184,
                "5": "TRGV3*01",
                "5del": 0,
                "5end": 178,
                "N": 5,
                "_evalue": "6.001938e-20",
                "_evalue_left": "6.001938e-20",
                "_evalue_right": "1.421484e-190",
                "affectSigns": {
                    "seq": "------------                      ------------------------------------------------------------------------------------------ -                                                                             ",
                    "start": 0,
                    "stop": 213
                },
                "affectValues": {
                    "seq": "gggggggggggg______________________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?G_____________________________________________________________________________",
                    "start": 0,
                    "stop": 213
                },
                "cdr3": {
                    "aa": "ATWDRRGIRKL",
                    "start": 165,
                    "stop": 197
                },
                "junction": {
                    "aa": "CATWDRRGIRKLF",
                    "productive": true,
                    "start": 162,
                    "stop": 200
                }
            },
            "seg_stat": {
                "2": 3,
                "3": 1804
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACAGGCGGGGTATAAGAAAACTCTTTGGCAGTGGAACAA",
            "top": 52
        },
        {
            "_average_read_length": [
                214.967025756836
            ],
            "_coverage": [
                1.00480532646179
            ],
            "_coverage_info": [
                "216 bp (100% of 215.0 bp)"
            ],
            "germline": "TRG",
            "id": "ATTACTGTGCCACCTGGGACGGGCAGGGTTATAAGAAACTCTTTGGCAGT",
            "name": "TRGV2*01 0/CAGGG/3 TRGJ1*02",
            "reads": [
                1698
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 3,
                "3start": 184,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 178,
                "N": 5,
                "_evalue": "5.498689e-49",
                "_evalue_left": "5.498689e-49",
                "_evalue_right": "1.532538e-195",
                "affectSigns": {
                    "seq": "----------------------               ------------------------------------------------------------------------------------------ --                                                                            ",
                    "start": 0,
                    "stop": 216
                },
                "affectValues": {
                    "seq": "gggggggggggggggggggggg_______________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG?GG____________________________________________________________________________",
                    "start": 0,
                    "stop": 216
                },
                "cdr3": {
                    "aa": "ATWDGQGYKKL",
                    "start": 165,
                    "stop": 197
                },
                "junction": {
                    "aa": "CATWDGQGYKKLF",
                    "productive": true,
                    "start": 162,
                    "stop": 200
                }
            },
            "seg_stat": {
                "2": 1585,
                "3": 113
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGGACGGGCAGGGTTATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 53
        },
        {
            "_average_read_length": [
                246.896530151367
            ],
            "_coverage": [
                1.00446939468384
            ],
            "_coverage_info": [
                "248 bp (100% of 246.9 bp)"
            ],
            "germline": "TRG",
            "id": "CTATTACTGTGCCACCTGGACGGGTCCCCCTGCCACTGGTTGGTTCAAGA",
            "name": "TRGV2*01 0/TCCCCCTG/3 TRGJP1*01",
            "reads": [
                1672
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 3,
                "3start": 186,
                "5": "TRGV2*01",
                "5del": 0,
                "5end": 177,
                "N": 8,
                "_evalue": "1.258885e-85",
                "_evalue_left": "2.478585e-178",
                "_evalue_right": "1.258885e-85",
                "affectSigns": {
                    "seq": "                                                                            ++ ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                       +++++++++++++        +++++++++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 248
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_______________________ggggggggggggg________ggggggggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 248
                },
                "cdr3": {
                    "aa": "ATWTGPPATGWFKI",
                    "start": 165,
                    "stop": 206
                },
                "junction": {
                    "aa": "CATWTGPPATGWFKIF",
                    "productive": true,
                    "start": 162,
                    "stop": 209
                }
            },
            "seg_stat": {
                "2": 1669,
                "3": 3
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCTGGACGGGTCCCCCTGCCACTGGTTGGTTCAAGATATTTTGCTGAAGGGACTAAGCTCATAGTAACTTCGCCTGGTAA",
            "top": 54
        },
        {
            "_average_read_length": [
                215.116775512695
            ],
            "_coverage": [
                1.00410580635071
            ],
            "_coverage_info": [
                "216 bp (100% of 215.1 bp)"
            ],
            "germline": "TRG",
            "id": "CTATTACTGTGCCACCCTCCTGCTCATTCCACGAGAGAAACTCTTTGGCA",
            "name": "TRGV2*02 6/CTCCTGCTCATTCCACGAG/8 TRGJ1*02",
            "reads": [
                1627
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 8,
                "3start": 189,
                "5": "TRGV2*02",
                "5del": 6,
                "5end": 169,
                "N": 19,
                "_evalue": "3.284017e-30",
                "_evalue_left": "2.291083e-169",
                "_evalue_right": "3.284017e-30",
                "affectSigns": {
                    "seq": "                                                                            ++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                             +++++++++++++++++",
                    "start": 0,
                    "stop": 216
                },
                "affectValues": {
                    "seq": "____________________________________________________________________________GG?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_____________________________ggggggggggggggggg",
                    "start": 0,
                    "stop": 216
                },
                "cdr3": {
                    "aa": "ATLLLIPREKL",
                    "start": 165,
                    "stop": 197
                },
                "junction": {
                    "aa": "CATLLLIPREKLF",
                    "productive": true,
                    "start": 162,
                    "stop": 200
                }
            },
            "seg_stat": {
                "2": 1583,
                "3": 44
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCAGTACTATGACTCCTACAACTCCAAGGTTGTGTTGGAATCAGGAGTCAGTCCAGGGAAGTATTATACTTACGCAAGCACAAGGAACAACTTGAGATTGATACTGCAAAATCTAATTGAAAATGACTCTGGGGTCTATTACTGTGCCACCCTCCTGCTCATTCCACGAGAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 55
        },
        {
            "_average_read_length": [
                184.311798095703
            ],
            "_coverage": [
                0.808412730693817
            ],
            "_coverage_info": [
                "149 bp (80% of 184.3 bp)"
            ],
            "germline": "TRG",
            "id": "CCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTT",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                1594
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 126,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 125,
                "N": 0,
                "_evalue": "1.709349e-10",
                "_evalue_left": "2.923896e-177",
                "_evalue_right": "1.709349e-10",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++++++++                ++++++++     ",
                    "start": 0,
                    "stop": 149
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGGGGGGGG________________gggggggg_____",
                    "start": 0,
                    "stop": 149
                },
                "cdr3": {
                    "aa": "AACL#DWIKT",
                    "start": 113,
                    "stop": 141
                },
                "junction": {
                    "aa": "CAACL#DWIKTF",
                    "productive": false,
                    "start": 110,
                    "stop": 144
                }
            },
            "seg_stat": {
                "2": 1594
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTTTT",
            "top": 56
        },
        {
            "_average_read_length": [
                245.353286743164
            ],
            "_coverage": [
                0.876287400722504
            ],
            "_coverage_info": [
                "215 bp (87% of 245.4 bp)"
            ],
            "germline": "TRG",
            "id": "TATTACTGTGCCACCTGGGACCCCCGGGCGGGTAGTGATTGGATCAAGAC",
            "name": "TRGV3*01 3/CCCCGGGCGG/3 TRGJP2*01",
            "reads": [
                1537
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 3,
                "3start": 186,
                "5": "TRGV3*01",
                "5del": 3,
                "5end": 175,
                "N": 10,
                "_evalue": "2.649726e-21",
                "_evalue_left": "8.161363e-184",
                "_evalue_right": "2.649726e-21",
                "affectSigns": {
                    "seq": "                                                                             + +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                    +++++++++++++      ",
                    "start": 0,
                    "stop": 215
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________G?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG____________________ggggggggggggg______",
                    "start": 0,
                    "stop": 215
                },
                "cdr3": {
                    "aa": "ATWDPRAGSDWIKT",
                    "start": 165,
                    "stop": 206
                },
                "junction": {
                    "aa": "CATWDPRAGSDWIKTF",
                    "productive": true,
                    "start": 162,
                    "stop": 209
                }
            },
            "seg_stat": {
                "2": 1508,
                "3": 29
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACCCCCGGGCGGGTAGTGATTGGATCAAGACGTTTTGCAAA",
            "top": 57
        },
        {
            "_average_read_length": [
                168.286087036133
            ],
            "_coverage": [
                0.962646424770355
            ],
            "_coverage_info": [
                "162 bp (96% of 168.3 bp)"
            ],
            "germline": "TRG",
            "id": "AAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTGCT",
            "name": "TRGV10*01 13//5 TRGJP1*01",
            "reads": [
                1531
            ],
            "seg": {
                "3": "TRGJP1*01",
                "3del": 5,
                "3start": 115,
                "5": "TRGV10*01",
                "5del": 13,
                "5end": 114,
                "N": 0,
                "_evalue": "2.345480e-64",
                "_evalue_left": "2.766642e-186",
                "_evalue_right": "2.345480e-64",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++            ++++++++++        +++++++++++++++++++",
                    "start": 0,
                    "stop": 162
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG____________gggggggggg________ggggggggggggggggggg",
                    "start": 0,
                    "stop": 162
                }
            },
            "seg_stat": {
                "2": 1263,
                "3": 268
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTACTACTGTGCGGACTGGTTGGTTCAAGATATTGCTGAAGGGACTAAGCTCATAGTAACT",
            "top": 58
        },
        {
            "_average_read_length": [
                182.964859008789
            ],
            "_coverage": [
                0.923674643039703
            ],
            "_coverage_info": [
                "169 bp (92% of 183.0 bp)"
            ],
            "germline": "TRG",
            "id": "CCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTG",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                1480
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 126,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 125,
                "N": 0,
                "_evalue": "5.279890e-49",
                "_evalue_left": "2.923896e-177",
                "_evalue_right": "5.279890e-49",
                "affectSigns": {
                    "seq": "            +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++++++++                ++++++++         ++++++++++++++++",
                    "start": 0,
                    "stop": 169
                },
                "affectValues": {
                    "seq": "____________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGGGGGGGG________________gggggggg_________gggggggggggggggg",
                    "start": 0,
                    "stop": 169
                },
                "cdr3": {
                    "aa": "AACLGIGSRR",
                    "start": 113,
                    "stop": 142
                },
                "junction": {
                    "aa": "CAACLGIGSRRF",
                    "productive": true,
                    "start": 110,
                    "stop": 145
                }
            },
            "seg_stat": {
                "2": 1480
            },
            "sequence": "AGCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTTTTGCAAAAGGGACTAGGCTCATAGT",
            "top": 59
        },
        {
            "_average_read_length": [
                330.151550292969
            ],
            "_coverage": [
                0.981367468833923
            ],
            "_coverage_info": [
                "324 bp (98% of 330.2 bp)"
            ],
            "germline": "IGH",
            "id": "TTACTGTGCAAGAGATATGAGTGGTAACTGGAACCTCCTACGGTATGGAC",
            "name": "IGHV3-13*01 0/TATGAGTGG/4 IGHD1-20*01 3//10 IGHJ6*02",
            "reads": [
                1478
            ],
            "seg": {
                "3": "IGHJ6*02",
                "3del": 10,
                "3start": 269,
                "4": "IGHD1-20*01",
                "4delLeft": 4,
                "4delRight": 3,
                "4end": 268,
                "4start": 259,
                "5": "IGHV3-13*01",
                "5del": 0,
                "5end": 249,
                "N1": 9,
                "N2": 0,
                "_evalue": "2.286799e-100",
                "_evalue_left": "2.286799e-100",
                "_evalue_right": "1.610961e-215",
                "affectSigns": {
                    "seq": "  --------------------------------------                                  ------------------------------------------------------------------------------------------                                                                                                                                                    ",
                    "start": 0,
                    "stop": 324
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh__________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH____________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 324
                },
                "cdr3": {
                    "aa": "ARDMSGNWN#SYGMDV",
                    "start": 243,
                    "stop": 288
                },
                "junction": {
                    "aa": "CARDMSGNWN#SYGMDVW",
                    "productive": false,
                    "start": 240,
                    "stop": 291
                }
            },
            "seg_stat": {
                "2": 483,
                "3": 995
            },
            "sequence": "GGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTCAGTAGCTACGACATGCACTGGGTCCGCCAAGCTACAGGAAAAGGTCTGGAGTGGGTCTCAGCTATTGGTACTGCTGGTGACACATACTATCCAGGCTCCGTGAAGGGCCGATTCACCATCTCCAGAGAAAATGCCAAGAACTCCTTGTATCTTCAAATGAACAGCCTGAGAGCCGGGGACACGGCTGTGTATTACTGTGCAAGAGATATGAGTGGTAACTGGAACCTCCTACGGTATGGACGTCTGGGGCCAAGGGACCCTGGTCACCGTCTCCTCAGGT",
            "top": 60
        },
        {
            "_average_read_length": [
                179.500350952148
            ],
            "_coverage": [
                0.997212529182434
            ],
            "_coverage_info": [
                "179 bp (99% of 179.5 bp)"
            ],
            "germline": "TRG",
            "id": "CCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTGCAA",
            "name": "TRGV10*01 4//8 TRGJP2*01",
            "reads": [
                1397
            ],
            "seg": {
                "3": "TRGJP2*01",
                "3del": 8,
                "3start": 125,
                "5": "TRGV10*01",
                "5del": 4,
                "5end": 124,
                "N": 0,
                "_evalue": "1.811316e-56",
                "_evalue_left": "9.595058e-178",
                "_evalue_right": "1.811316e-56",
                "affectSigns": {
                    "seq": "           +            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++        +++++++++                +++++++            +++++++++++++++++++++    ",
                    "start": 0,
                    "stop": 179
                },
                "affectValues": {
                    "seq": "___________G____________GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG________GGGGGGGGG________________ggggggg____________ggggggggggggggggggggg____",
                    "start": 0,
                    "stop": 179
                }
            },
            "seg_stat": {
                "2": 1397
            },
            "sequence": "GCATGGGTAAGACAAGCAACAAAGTGGAGGCAAGAAAGAATTCTCAAACTCTCACTTCAATCCTTACCATCAAGTCCGTAGAGAAAGAAGACATGGCCGTTTTACTACTGTGCTGCGTGTCTGGGGATTGGATCAAGACGTTGCAAAGGGACTAGGCTCATAGTAACTTCGCCTGGTAA",
            "top": 61
        },
        {
            "_average_read_length": [
                213.868118286133
            ],
            "_coverage": [
                1.0052924156189
            ],
            "_coverage_info": [
                "215 bp (100% of 213.9 bp)"
            ],
            "germline": "TRG",
            "id": "ATTACTGTGCCACCTGGGACAGGCGGGGTATAAGAAACTCTTTGGCAGTG",
            "name": "TRGV3*01 0/CGGGG/4 TRGJ1*02",
            "reads": [
                1380
            ],
            "seg": {
                "3": "TRGJ1*02",
                "3del": 4,
                "3start": 184,
                "5": "TRGV3*01",
                "5del": 0,
                "5end": 178,
                "N": 5,
                "_evalue": "2.529960e-46",
                "_evalue_left": "9.575354e-193",
                "_evalue_right": "2.529960e-46",
                "affectSigns": {
                    "seq": "                                                                             + ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++               +++++++++++++++++++++",
                    "start": 0,
                    "stop": 215
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________G?GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_______________ggggggggggggggggggggg",
                    "start": 0,
                    "stop": 215
                },
                "cdr3": {
                    "aa": "ATWDRR#YKKL",
                    "start": 165,
                    "stop": 196
                },
                "junction": {
                    "aa": "CATWDRR#YKKLF",
                    "productive": false,
                    "start": 162,
                    "stop": 199
                }
            },
            "seg_stat": {
                "2": 1361,
                "3": 19
            },
            "sequence": "GGAAGGCCCCACAGCGTCTTCTGTACTATGACGTCTCCACCGCAAGGGATGTGTTGGAATCAGGACTCAGTCCAGGAAAGTATTATACTCATACACCCAGGAGGTGGAGCTGGATATTGAGACTGCAAAATCTAATTGAAAATGATTCTGGGGTCTATTACTGTGCCACCTGGGACAGGCGGGGTATAAGAAACTCTTTGGCAGTGGAACAACAC",
            "top": 62
        },
        {
            "_average_read_length": [
                314.438507080078
            ],
            "_coverage": [
                0.801428556442261
            ],
            "_coverage_info": [
                "252 bp (80% of 314.4 bp)"
            ],
            "germline": "IGH",
            "id": "TATTACTGTGCGAGACCTCCTCACGCTATGGTTACTCTACTTTGACTACT",
            "name": "IGHV4-39*01 1/CTCCTCAC/9 IGHD5-18*01 0/T/1 IGHJ4*02",
            "reads": [
                1350
            ],
            "seg": {
                "3": "IGHJ4*02",
                "3del": 1,
                "3start": 203,
                "4": "IGHD5-18*01",
                "4delLeft": 9,
                "4delRight": 0,
                "4end": 201,
                "4start": 191,
                "5": "IGHV4-39*01",
                "5del": 1,
                "5end": 182,
                "N1": 8,
                "N2": 1,
                "_evalue": "5.071099e-92",
                "_evalue_left": "5.071099e-92",
                "_evalue_right": "0.000000e+00",
                "affectSigns": {
                    "seq": "  -----------------------------------                                ---------------------------------------------------------------------------------------------------------------                                                            ",
                    "start": 0,
                    "stop": 252
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH____________________________________________________________",
                    "start": 0,
                    "stop": 252
                },
                "cdr3": {
                    "aa": "ARPPHAMVT#YFDY",
                    "start": 177,
                    "stop": 216
                },
                "junction": {
                    "aa": "CARPPHAMVT#YFDYW",
                    "productive": false,
                    "start": 174,
                    "stop": 219
                }
            },
            "seg_stat": {
                "2": 555,
                "3": 795
            },
            "sequence": "TCCGCCAGCCCCCAGGGAAGGGGCTGGAGTGGATTGGGAGTATCTATTATAGTGGGAGCACCTACTACAACCCGTCCCTCAAGAGTCGAGTCACCATATCCGTAGACACGTCCAAGAACCAGTTCTCCCTGAAGCTGAGCTCTGTGACCGCCGCAGACACGGCTGTGTATTACTGTGCGAGACCTCCTCACGCTATGGTTACTCTACTTTGACTACTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 63
        },
        {
            "_average_read_length": [
                351.147277832031
            ],
            "_coverage": [
                0.740430057048798
            ],
            "_coverage_info": [
                "260 bp (74% of 351.1 bp)"
            ],
            "germline": "IGH",
            "id": "GTGTATTACTGTGCGAGAGACGGGGCCTGGGACTGGTTCGACCCTGGGGC",
            "name": "IGHV3-21*01 0/CGGGGCCTGGG/3 IGHJ5*02",
            "reads": [
                1324
            ],
            "seg": {
                "3": "IGHJ5*02",
                "3del": 3,
                "3start": 232,
                "5": "IGHV3-21*01",
                "5del": 0,
                "5end": 220,
                "N": 11,
                "_evalue": "3.011335e-10",
                "_evalue_left": "1.101537e-223",
                "_evalue_right": "3.011335e-10",
                "affectSigns": {
                    "seq": "                                                                             ----                                      ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                       +         ++++++",
                    "start": 0,
                    "stop": 260
                },
                "affectValues": {
                    "seq": "_____________________________________________________________________________hhhh______________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH_______________________h_________hhhhhh",
                    "start": 0,
                    "stop": 260
                },
                "cdr3": {
                    "aa": "ARDGAW#LVRP",
                    "start": 214,
                    "stop": 245
                },
                "junction": {
                    "aa": "CARDGAW#LVRPW",
                    "productive": false,
                    "start": 211,
                    "stop": 248
                }
            },
            "seg_stat": {
                "2": 1316,
                "3": 8
            },
            "sequence": "GGATTCACCTTCAGTAGCTATAGCATGAACTGGGTCCGCCAGGCTCCAGGGAAGGGGCTGGAGTGGGTCTCATCCATTAGTAGTAGTAGTAGTTACATATACTACGCAGACTCAGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCACTGTATCTGCAAATGAACAGCCTGAGAGCCGAGGACACGGCTGTGTATTACTGTGCGAGAGACGGGGCCTGGGACTGGTTCGACCCTGGGGCCAGGGAACC",
            "top": 64
        },
        {
            "_average_read_length": [
                346.782653808594
            ],
            "_coverage": [
                1.00927770137787
            ],
            "_coverage_info": [
                "350 bp (100% of 346.8 bp)"
            ],
            "germline": "IGH",
            "id": "CTGTGCGAGAGGTAGGGCCTTTGGACGCAGTGGCTCTAATACTACTTTGA",
            "name": "IGHV4-59*01 1/GTAGGGCCTTTGGAC/7 IGHD6-19*01 5/CTAAT/0 IGHJ4*02",
            "reads": [
                819
            ],
            "seg": {
                "3": "IGHJ4*02",
                "3del": 0,
                "3start": 300,
                "4": "IGHD6-19*01",
                "4delLeft": 7,
                "4delRight": 5,
                "4end": 294,
                "4start": 286,
                "5": "IGHV4-59*01",
                "5del": 1,
                "5end": 270,
                "N1": 15,
                "N2": 5,
                "_evalue": "2.474235e-92",
                "_evalue_left": "2.474235e-92",
                "_evalue_right": "0.000000e+00",
                "affectSigns": {
                    "seq": "  ------------------------------------                                        ----------------------------------------------------------------------------------------------------    -                                                                                                                                                           ",
                    "start": 0,
                    "stop": 350
                },
                "affectValues": {
                    "seq": "__hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh________________________________________HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH____H___________________________________________________________________________________________________________________________________________________________",
                    "start": 0,
                    "stop": 350
                },
                "cdr3": {
                    "aa": "ARGRAFGRSGSN#YFDY",
                    "start": 265,
                    "stop": 314
                },
                "junction": {
                    "aa": "CARGRAFGRSGSN#YFDYW",
                    "productive": false,
                    "start": 262,
                    "stop": 317
                }
            },
            "seg_stat": {
                "2": 377,
                "3": 442
            },
            "sequence": "GGCCCAGGACTGGTGAAGCCTTCGGAGACCCTGTCCCTCACCTGCACTGTCTCTGGTGGCTCCATCAGTAGTTACTACTGGAGCTGGATCCGGCAGCCCCCAGGGAAGGGACTGGAGTGGATTGGGTATATCTATTACAGTGGGAGCACCAACTACAACCCCTCCCTCAAGAGTCGAGTCACCATATCAGTAGACACGTCCAAGAACCAGTTCTCCCTGAAGCTGAGCTCTGTGACCGCTGCGGACACGGCCGTGTATTACTGTGCGAGAGGTAGGGCCTTTGGACGCAGTGGCTCTAATACTACTTTGACTACTGGGGCCAGGGAACCCTGGTCACCGTCTCCTCAGGT",
            "top": 99
        }
    ],
    "diversity": {
        "index_Ds_diversity": 0.963561058044434,
        "index_E_equitability": 0.388798922300339,
        "index_H_entropy": 5.63023193998924
    },
    "germlines": {
        "custom": {
            "3": [],
            "4": [],
            "5": [],
            "shortcut": "X"
        }
    },
    "reads": {
        "germline": {
            "IGH": [
                554178
            ],
            "IGK": [
                0
            ],
            "IGL": [
                0
            ],
            "TRA": [
                0
            ],
            "TRB": [
                0
            ],
            "TRD": [
                0
            ],
            "TRG": [
                1391438
            ]
        },
        "segmented": [
            1945616
        ],
        "total": [
            2390273
        ]
    },
    "samples": {
        "commandline": [
            "/home/vidjil/vidjil-release//vidjil -o /mnt/result/tmp/out-006960/ -b 006960 -c clones -3 -z 100 -r 1 -g/home/vidjil/vidjil-release//germline/ -e 1 -w 50 /mnt/upload/uploads/sequence_file.data_file.99a1cde4ae44777d.4c494c2d4c332d302e66617374712e677a.gz "
        ],
        "log": [
            "  ==> junction detected in 1957936 reads (81.9%)\n  ==> found 100265 50-windows in 1945616 reads (81.4% of 2390273 reads)\n                          reads av. len     clones clo/rds\n  IGH               ->   554178   315.0      31096   0.056\n  IGK               ->        0       -          0       -\n  IGL               ->        0       -          0       -\n  TRA               ->        0       -          0       -\n  TRB               ->        0       -          0       -\n  TRD               ->        0       -          0       -\n  TRG               ->  1391438   197.7      69169   0.050\n\n  SEG               ->  1945616   231.1\n  SEG_+             ->  1002184   218.2\n  SEG_-             ->   943432   244.8\n\n  UNSEG too short   ->     5526    10.1\n  UNSEG strand      ->     1547   213.0\n  UNSEG too few V/J ->   174669   116.9\n  UNSEG only V/5'   ->   193622   241.2\n  UNSEG only J/3'   ->    56820    68.7\n  UNSEG < delta_min ->        0       -\n  UNSEG ambiguous   ->      153   346.2\n  UNSEG too short w ->    12320   212.6\n"
        ],
        "number": 1,
        "original_names": [
            "/mnt/upload/uploads/sequence_file.data_file.99a1cde4ae44777d.4c494c2d4c332d302e66617374712e677a.gz"
        ],
        "producer": [
            "vidjil 2016.03"
        ],
        "run_timestamp": [
            "2016-03-10 14:03:11"
        ]
    },
    "vidjil_json_version": "2014.10"
};