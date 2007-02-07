#!/usr/bin/perl -w

use Fcntl ':flock';

# START USER EDITS

# absolute path to folder files will be uploaded to.
# WINDOWS users, your path would like something like : images\\uploads
# UNIX    users, your path would like something like : /home/www/images/uploads
# do not end the path with any slashes and if you're on a UNIX serv, make sure
# you CHMOD each folder in the path to 777

$lockfileName = "/.../.lock";
$logFile =    "/.../uploadLog.txt";
$uidMapFile = "/.../mylarUidMap.txt";

$dir = "/isd/se2/project/mylar/userStudy/upload";  

# if you would like to be notified of uploads, enter your email address
# between the SINGLE quotes. leave this blank if you would not like to be notified
$notify = '';

# UNIX users, if you entered a value for $notify, you must also enter your
# server's sendmail path. It usually looks something like : /usr/sbin/sendmail
$send_mail_path = "";

# WINDOWS users, if you entered a value for $notify, you must also enter your
# server's SMTP path. It usually looks something like : mail.servername.com
$smtp_path = "";

# file types allowed, enter each type on a new line
# Enter the word "ALL" in uppercase, to accept all file types.
@types = qw~
zip
txt
~;

####################################################################
#    END USER EDITS
####################################################################

$OS = $^O; # operating system name
if($OS =~ /darwin/i) { $isUNIX = 1; }
elsif($OS =~ /win/i) { $isWIN = 1; }
else {$isUNIX = 1;}
	
if($isWIN){ $S{S} = "\\\\"; }
else { $S{S} = "/";} # seperator used in paths

unless (-d "$dir"){
	mkdir ("$dir", 0777); # unless the dir exists, make it ( and chmod it on UNIX )
	chmod(0777, "$dir");
}


unless (-d "$dir"){
	# if there still is no dir, the path entered by the user is wrong and the upload will fail
	# send back an error code
	# unauthorized, uid not correct
	exit;
}

use CGI; # load the CGI.pm module
my $GET = new CGI; # create a new object
my @VAL = $GET->param; #get all form field names

foreach(@VAL){
	$FORM{$_} = $GET->param($_); # put all fields and values in hash 
}

my($uid) = "";

my @files;
foreach(keys %FORM){
	# check for the parameter name
	# This must be MYLARa where is a a number
	if($_ =~ /^MYLAR(\d+)/){
		$uid = $1;
		if(&checkUID($uid) == 1)
		{
			push(@files, $_); # place the field NAME in an array
		}
		else
		{
			# unauthorized, uid not correct
			print "Content-type: text/plain", "\n";
			print "Status: 401 Unauthorized", "\n\n";
			print "UID Incorrect","\n";
			exit;
		}
	}
}


if(!$VAL[0]){
	# no file to upload so exit with an error
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented", "\n\n";
	print "Upload Failed - no file to upload","\n";

	exit;
}


my $failed; # results string = false
my $selected; # num of files selected by user

#################################################################### 

#################################################################### 

foreach (@files){
	# upload each file, pass the form field NAME if it has a value
	if($GET->param($_)){
		# if the form field contains a file name &psjs_upload subroutine
		# the file's name and path are passed to the subroutine 
		$returned = &psjs_upload($_); 
		if($returned =~ /^Success/i){
			# if the $returned message begins with "Success" the upload was succssful
			# remove the word "Success" and any spaces and we're left with the filename   
			$returned =~ s/^Success\s+//;
			push(@success, $returned);
		} else {
			# else if the word "success" is not returned, the message is the error encountered. 
			# add the error to the $failed scalar
			$failed .= $returned;
		}
		$selected++; # increment num of files selected for uploading by user
	}
}

if(!$selected){
	# no files were selected by user, so nothing is returned to either variable
	$failed .= qq~No files were selected for uploading~;
}

# if no error message is return ed, the upload was successful

my ($fNames, $aa, $bb, @current, @currentfiles );

if($failed){

	# file failed to upload return error
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented", "\n\n";
	print "Upload Failed","\n";
	
} else {
	# upload was successful
	# log the success and return success code
	
	# send email if valid email was entered
	if(check_email($notify)){
		
		# TODO send an appropriate message
		
		# enter the message you would like to receive
		my $message = qq~
		The following files were uploaded to your server :
		~; 
		
		foreach(@success){
			$message .= qq~
			$dir/$_	
			~;
		}
		
		if($isUNIX){
			$CONFIG{mailprogram} = $send_mail_path;
			# enter your e-mail name here if you like
			# from e-mail, from name, to e-mail, to name, subject, body
			&send_mail($notify, 'Demo Upload', $notify, 'Demo Upload', 'Upload Notification', $message);
			
		} else {
			$CONFIG{smtppath} = $smtp_path;
			&send_mail_NT($notify, 'Your Name', $notify, 'Your Name', 'Upload Notification', $message);
		}
	}
	
	# NEED TO LOG THE UPLOAD
	foreach(@success){
		&log($uid . "\t" . "$dir/$_");
	}
	
	
	print "Content-type: text/plain". "\n";
	print "Status: 200 OK", "\n\n";
	print "Upload Success","\n";
}

#################################################################### 

#################################################################### 

sub psjs_upload {

	my $filename = $GET->param($_[0]); 
	$filename =~ s/.*[\/\\](.*)/$1/; 
	my $upload_filehandle = $GET->upload($_[0]); 
	# if $file_type matchs one of the types specified, make the $type_ok var true
	for($b = 0; $b < @types; $b++){
		if($filename =~ /^.*\.$types[$b]$/i){
			$type_ok++;
		}
		if($types[$b] eq "ALL"){
			$type_ok++; # if ALL keyword is found, increment $type_ok var.
		}
	}
	
	# if ok, check if overwrite is allowed
	if($type_ok){
	    if(open UPLOADFILE, ">$dir/$filename"){

	    	binmode UPLOADFILE; 

	    	while ( <$upload_filehandle> ) 
	    	{
				print UPLOADFILE; 
	    	} 
   		    close UPLOADFILE; 
	    }else {
	    	return qq~Error opening file on the server~; 
	    }
	} else {
		return qq~Bad file type~; 
	}
			
	# check if file has actually been uploaded, by checking the file has a size
	if(-s "$dir/$filename"){
		return qq~Success $filename~; #success 
	} else {
		# delete the file as it has no content
		# user probably entered an incorrect path to file
		return qq~Upload failed : No data in $filename. No size on server's copy of file. 
		Check the path entered.~; 
	}
}

#################################################################### 

#################################################################### 

sub check_existence {
	# $dir,$filename,$newnum are the args passed to this sub
	my ($dir,$filename,$newnum) = @_;
	
	my (@file_type, $file_type, $exists, $bareName); 
	# declare some vars we will use later on in this sub always use paranthesis 
	# when declaring more than one var! Some novice programmers will tell you 
	# this is not necessary. Tell them to learn how to program.
	
	if(!$newnum){$newnum = "0";} # new num is empty in first call, so set it to 0
	
	# read dir and put all files in an array (list)
	opendir(DIR, "$dir");
	@existing_files =  readdir(DIR);
	closedir(DIR);
	
	# if the filename passed exists, set $exists to true or 1
	foreach(@existing_files){
		if($_ eq $filename){
			$exists = 1;
		}
	}
	
	# if it exists, we need to rename the file being uploaded and then recheck it to 
	# make sure the new name does not exist
	if($exists){
		$newnum++; # increment new number (add 1)

		# get the extension
		@file_type   = split(/\./, $filename); # split the dots and add inbetweens to a list
		# put the first element in the $barename var
		$bareName    = $file_type[0]; 
		# we can assume everything after the last . found is the extension
		$file_type   = $file_type[$#file_type]; 
		# $#file_type is the last element (note the pound or hash is used)
		
		# remove all numbers from the end of the $bareName
		$bareName =~ s/\d+$//ig;
		
		# concatenate a new name using the barename + newnum + extension 
		$filename = $bareName . $newnum . '.' . $file_type;
		
		# reset $exists to 0 because the new file name is now being checked
		$exists = 0;
		
		# recall this subroutine
		&check_existence($dir,$filename,$newnum);
	} else {
		# the $filename, whether the first or one hundreth call, now does not exist
		# so return the name to be used
		return ($filename);
	}
}

#################################################################### 

#################################################################### 

sub send_mail {
	my ($from_email, $from_name, $to_email, $to_name, $subject, $message ) = @_;
	
	if(open(MAIL, "|$CONFIG{mailprogram} -t")) {
		print MAIL "From: $from_email ($from_name)\n";
		print MAIL "To: $to_email ($to_name)\n";
		print MAIL "Subject: $subject\n";
		print MAIL "$message\n\nSubmitter's IP Address : $ENV{REMOTE_ADDR}";
		close MAIL;
		return(1);
	} else {
		return;
	}
}

#################################################################### 

#################################################################### 

sub send_mail_NT {
	
	my ($from_email, $from_name, $to_email, $to_name, $subject, $message ) = @_;
	
	my ($SMTP_SERVER, $WEB_SERVER, $status, $err_message);
	use Socket; 
    $SMTP_SERVER = "$CONFIG{smtppath}";                                 
	
	# correct format for "\n"
    local($CRLF) = "\015\012";
    local($SMTP_SERVER_PORT) = 25;
    local($AF_INET) = ($] > 5 ? AF_INET : 2);
    local($SOCK_STREAM) = ($] > 5 ? SOCK_STREAM : 1);
#    local(@bad_addresses) = ();
    $, = ', ';
    $" = ', ';

    $WEB_SERVER = "$CONFIG{smtppath}\n";
    chop ($WEB_SERVER);

    local($local_address) = (gethostbyname($WEB_SERVER))[4];
    local($local_socket_address) = pack('S n a4 x8', $AF_INET, 0, $local_address);

    local($server_address) = (gethostbyname($SMTP_SERVER))[4];
    local($server_socket_address) = pack('S n a4 x8', $AF_INET, $SMTP_SERVER_PORT, $server_address);

    # Translate protocol name to corresponding number
    local($protocol) = (getprotobyname('tcp'))[2];

    # Make the socket filehandle
    if (!socket(SMTP, $AF_INET, $SOCK_STREAM, $protocol)) {
        return;
    }

	# Give the socket an address
	bind(SMTP, $local_socket_address);
	
	# Connect to the server
	if (!(connect(SMTP, $server_socket_address))) {
		return;
	}
	
	# Set the socket to be line buffered
	local($old_selected) = select(SMTP);
	$| = 1;
	select($old_selected);
	
	# Set regex to handle multiple line strings
	$* = 1;

    # Read first response from server (wait for .75 seconds first)
    select(undef, undef, undef, .75);
    sysread(SMTP, $_, 1024);
	#print "<P>1:$_";

    print SMTP "HELO $WEB_SERVER$CRLF";
    sysread(SMTP, $_, 1024);
	#print "<P>2:$_";

	while (/(^|(\r?\n))[^0-9]*((\d\d\d).*)$/g) { $status = $4; $err_message = $3}
	if ($status != 250) {
		return;
	}

	print SMTP "MAIL FROM:<$from_email>$CRLF";

	sysread(SMTP, $_, 1024);
	#print "<P>3:$_";
	if (!/[^0-9]*250/) {
		return;
	}

    # Tell the server where we're sending to
	print SMTP "RCPT TO:<$to_email>$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>4:$_";
	/[^0-9]*(\d\d\d)/;

	# Give the server the message header
	print SMTP "DATA$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>5:$_";
	if (!/[^0-9]*354/) {
		return;
	}

	$message =~ s/\n/$CRLF/ig;
	
	print SMTP qq~From: $from_email ($from_name)$CRLF~;
	print SMTP qq~To: $to_email ($to_name)$CRLF~;
#	if($cc){
#		print SMTP "CC: $cc ($cc_name)\n";
#	}
	print SMTP qq~Subject: $subject$CRLF$CRLF~;
	print SMTP qq~$message~;

	print SMTP "$CRLF.$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>6:$_";
	if (!/[^0-9]*250/) {
		return;
	} else {
		return(1);
	}

	if (!shutdown(SMTP, 2)) {
		return;
    } 
}

#################################################################### 

#################################################################### 

sub check_email {
	my($fe_email) = $_[0];
	if($fe_email) {
		if(($fe_email =~ /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)|(\.$)/) ||
		($fe_email !~ /^.+@\[?(\w|[-.])+\.[a-zA-Z]{2,3}|[0-9]{1,3}\]?$/)) {
			return;
		} else { return(1) }
	} else {
		return;
	}
}
#################################################################### 

#################################################################### 

sub checkUID {
	my($uid) = $_[0];

	open(LOCKFILE, $lockfileName);
    	flock(LOCKFILE, LOCK_EX);
    	
	open(UIDMAP, $uidMapFile) || die "Can't open Log File: $!\n"; 
	my($found) = 0;
	my(@lines) = <UIDMAP>;
	my($line) = "";
	foreach $line (@lines)
	{
		if($line =~ m/^$uid\t(.*)\t(.*)\t(.*)$/)
		{
			$found = 1;
			last;
		}
	}
	close UIDMAP;

	flock(LOCKFILE, LOCK_UN);
	close LOCKFILE;
	return $found;
}

#################################################################### 

#################################################################### 

sub log {
	open(LOCKFILE, $lockfileName);
	flock(LOCKFILE, LOCK_EX);
    	
	open(LOG, "+<$logFile") || die "Can't open Log File: $!\n"; 
    
	seek LOG, 0, 2;
	print LOG $_[0] . "\t\t";	

	my ($sec,$min,$hour,$mday,$mon,$year, $wday,$yday,$isdst) = localtime time;

	# update the year so that it is correct since it perl
	# has a 1900 yr offset	
	$year += 1900;

	# update the month since it is 0 based in perl	
	$mon += 1;
	
	printf LOG "%02d/%02d/%04d %02d:%02d:%02d\n", $mday, $mon, $year, $hour, $min, $sec;
	
	close LOG;
	
	flock(LOCKFILE, LOCK_UN);
    close LOCKFILE;
}
