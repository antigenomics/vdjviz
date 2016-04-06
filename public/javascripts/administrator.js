(function() {
    var application = angular.module('administratorPanel', []);

    application.factory('administrator', ['$http', 'notifications', function($http, notifications) {

        var accounts = [];
        var isNewCreating = false;
        var isEdit = false;

        $http.get('/account/administrator/accounts')
            .success(function(response) {
                angular.extend(accounts, response);
            })
            .error(function() {
                notifications.addErrorNotification('', 'Access denied');
                setTimeout(function() {
                    window.location.replace('/')
                }, 1000);
            });

        function getAccounts() {
            return accounts;
        }

        function deleteAccount(account) {
            var index = accounts.indexOf(account);
            if (index < 0) return;
            var r = window.confirm('Are you sure?');
            if (r === false) return;
            if (account.userName === administratorName) {
                notifications.addErrorNotification('Delete user', 'You cannot delete your own account');
                return;
            }
            $http.post('/account/administrator/delete', { userName: account.userName })
                .success(function(response) {
                    accounts.splice(index, 1);
                    notifications.addSuccessNotification('Delete user', 'Successfully deleted');
                })
                .error(function(response) {
                    notifications.addErrorNotification('Delete user', 'Error deleting user ' + account.userName);
                })
        }

        function createAccount(newAccount) {
            var r = window.confirm('Are you sure?');
            if (r === false) return;
            $http.post('/account/administrator/create', newAccount)
                .success(function() {
                    notifications.addSuccessNotification('Delete user', 'Successfully created');
                    isNewCreating = false;
                    addNew(newAccount);
                })
                .error(function(message) {
                    notifications.addErrorNotification('Delete user', message);
                })
        }

        function editSubmit(acc) {
            var r = window.confirm('Are you sure?');
            if (r === false) return;
            $http.post('/account/administrator/edit', acc)
                .success(function() {
                    notifications.addSuccessNotification('Edit user', 'Successfully edited');
                    isEdit = false;
                    editOld(acc);
                })
                .error(function(message) {
                    notifications.addErrorNotification('Edit user', message);
                })
        }

        function editOld(acc) {
            angular.forEach(accounts, function(account) {
                if (account.userName === acc.userName) {
                    account.userName = acc.userName;
                    account.privelegies = acc.privelegies;
                    account.filesInformation.maxFileSize = acc.maxFileSize;
                    account.filesInformation.maxFilesCount = acc.maxFilesCount;
                    account.filesInformation.maxClonotypesCount = acc.maxClonotypesCount;
                    account.maxSharedGroupsCount = acc.maxSharedFiles;
                }
            })
        }


        function addNew(account) {
            var newA = {
                userName: account.userName,
                filesCount: 0,
                sharedGroupsCount: 0,
                maxSharedGroupsCount: account.maxSharedFiles,
                privelegies: account.privelegies,
                filesInformation: {
                    maxFileSize: account.maxFileSize,
                    maxFilesCount: account.maxFilesCount,
                    maxClonotypesCount: account.maxClonotypesCount
                }
            };
            accounts.push(newA);
        }

        function isNewAccountCreating() {
            return isNewCreating;
        }

        function createCancel() {
            isNewCreating = false;
        }

        function createInit() {
            isNewCreating = true;
        }

        function editInit() {
            isEdit = true;
        }

        function editCancel() {
            isEdit = false;
        }

        function isEditAccount() {
            return isEdit;
        }

        return {
            getAccounts: getAccounts,
            deleteAccount: deleteAccount,
            createAccount: createAccount,
            isNewAccountCreating: isNewAccountCreating,
            createCancel: createCancel,
            createInit: createInit,
            editInit: editInit,
            editCancel: editCancel,
            isEditAccount: isEditAccount,
            editSubmit: editSubmit,
            editOld: editOld
        }

    }]);

    application.directive('administrator', function() {
        return {
            restrict: 'E',
            controller: ['$scope', 'administrator', function($scope, admin) {


                $scope.accounts = admin.getAccounts;
                $scope.deleteAccount = admin.deleteAccount;
                $scope.isNewAccountCreating = admin.isNewAccountCreating;
                $scope.createCancel = admin.createCancel;

                $scope.editCancel = admin.editCancel;
                $scope.isEditAccount = admin.isEditAccount;

                $scope.editSubmit = editSubmit;
                $scope.createAccount = createAccount;
                $scope.isActiveAccount = isActiveAccount;
                $scope.createInit = createInit;
                $scope.editInit = editInit;

                $scope.newAccount = {
                    userName: '',
                    password: '',
                    maxFileSize: 0,
                    maxFilesCount: 0,
                    maxClonotypesCount: 0,
                    maxSharedFiles: 0,
                    privelegies: false
                };

                $scope.editAccount = {
                    oldUserName: '',
                    userName: '',
                    password: '',
                    maxFileSize: 0,
                    maxFilesCount: 0,
                    maxClonotypesCount: 0,
                    maxSharedFiles: 0,
                    privelegies: false
                };

                function createInit() {
                    $scope.newAccount.userName = '';
                    $scope.newAccount.password = '';
                    $scope.newAccount.maxFileSize = 0;
                    $scope.newAccount.maxFilesCount = 0;
                    $scope.newAccount.maxClonotypesCount = 0;
                    $scope.newAccount.maxSharedFiles = 0;
                    $scope.newAccount.privelegies = false;
                    admin.createInit();
                }

                function editInit(account) {
                    $scope.editAccount.userName = account.userName;
                    $scope.editAccount.maxFileSize = account.filesInformation.maxFileSize;
                    $scope.editAccount.maxFilesCount = account.filesInformation.maxFilesCount;
                    $scope.editAccount.maxClonotypesCount = account.filesInformation.maxClonotypesCount;
                    $scope.editAccount.maxSharedFiles = account.maxSharedGroupsCount;
                    $scope.privelegies = account.privelegies;
                    admin.editInit();
                }

                function editSubmit() {
                    admin.editSubmit($scope.editAccount);
                }


                function createAccount() {
                    admin.createAccount($scope.newAccount);
                }

                function isActiveAccount(account) {
                    return account.userName === administratorName;
                }
            }]
        }
    });

    //Notifications factory and directive
    application.factory('notifications', ['$log', '$timeout', function($log, $timeout) {
        var notifications = [];


        function getNotifications() {
            return notifications;
        }

        function deleteNotification(notification) {
            var id = notifications.indexOf(notification);
            if (id >= 0) notifications.splice(id, 1);
        }

        function addNotification(type, header, body) {
            var newNotification = {
                type: type,
                header: header,
                body: body
            };
            notifications.push(newNotification);
            $timeout(function() {
                deleteNotification(newNotification);
            }, (function() {
                switch (type) {
                    case 'error':
                        return 5000;
                    default:
                        return 3000;
                }
            }()));
        }

        function addErrorNotification(header, body) {
            addNotification('error', header, body);
        }

        function addInfoNotification(header, body) {
            addNotification('info', header, body);
        }

        function addSuccessNotification(header, body) {
            addNotification('success', header, body);
        }

        return {
            getNotifications: getNotifications,
            deleteNotification: deleteNotification,
            addNotification: addNotification,
            addErrorNotification: addErrorNotification,
            addInfoNotification: addInfoNotification,
            addSuccessNotification: addSuccessNotification
        };
    }]);

    application.directive('notifications', function() {
        return {
            restrict: 'E',
            controller: ['$scope', 'notifications', function($scope, notifications) {
                $scope.notifications = notifications.getNotifications();
                $scope.deleteNotification = notifications.deleteNotification;

                $scope.isNotificationError = function(notification) {
                    return notification.type === 'error';
                };

                $scope.isNotificationInfo = function(notification) {
                    return notification.type === 'info';
                };

                $scope.isNotificationSuccess = function(notification) {
                    return notification.type === 'success';
                };

                $scope.isNotificationsExist = function() {
                    return $scope.notifications.length > 0;
                };

            }]
        };
    });
    //--------------------------------------------------------//

})();