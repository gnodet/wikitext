#!/usr/bin/perl -wT

use Fcntl ':flock';

# START USER EDITS

# absolute path to folder files will be uploaded to.
# WINDOWS users, your path would like something like : images\\uploads
# UNIX    users, your path would like something like : /home/www/images/uploads
# do not end the path with any slashes and if you're on a UNIX serv, make sure
# you CHMOD each folder in the path to 777

$lockfileName = ".lock";
$logFile = "/.../uploadLog.txt";
$uidMapFile = "/.../mylarUidMap.txt";
$nextUidFile = "/.../mylarNextUid.txt";
$allConsentedUsers = "/.../mylarUsers.txt";

# if you would like to be notified of uploads, enter your email address
# between the SINGLE quotes. leave this blank if you would not like to be notified
#$notify = '';

# UNIX users, if you entered a value for $notify, you must also enter your
# server's sendmail path. It usually looks something like : /usr/sbin/sendmail
#$send_mail_path = "";

# WINDOWS users, if you entered a value for $notify, you must also enter your
# server's SMTP path. It usually looks something like : mail.servername.com
#$smtp_path = "";


####################################################################
#    END USER EDITS
####################################################################

$OS = $^O; # operating system name
if($OS =~ /darwin/i) { $isUNIX = 1; }
elsif($OS =~ /win/i) { $isWIN = 1; }
else {$isUNIX = 1;}
	
if($isWIN){ $S{S} = "\\\\"; }
else { $S{S} = "/";} # seperator used in paths

use CGI; # load the CGI.pm module
my $GET = new CGI; # create a new object
my @VAL = $GET->param; #get all form field names

my($query_string) = "";
$query_string = $ENV{'QUERY_STRING'};

my($firstName);
my($lastName);
my($email_address);
my($job_function);
my($company_size);
my($company_buisness);
my($anonymousStr);
my($uid) = -1;
my($anonymous) = 0;
my($first);
my($second);
my($third);
my($fourth);
my($fifth);
my($sixth);
my($seventh);
	
if($query_string =~ m/^(.+)\&(.+)\&(.+)\&(.+)\&(.+)\&(.+)\&(.+)$/)
{
	$first = $1;
	$second = $2;
	$third = $3;
	$fourth = $4;
	$fifth = $5;
	$sixth = $6;
	$seventh = $7;
}
else
{
	# error, query string is wrong
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented", "\n\n";
	exit;
}

if($first =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif($second =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif ($third =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif ($fourth =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif ($fifth =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif ($sixth =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}
elsif ($seventh =~ m/^firstName\=(.+)$/){
	$firstName = $1;
}

if($first =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif($second =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif ($third =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif ($fourth =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif ($fifth =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif ($sixth =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}
elsif ($seventh =~ m/^lastName\=(.+)$/){
	$lastName = $1;
}

if($first =~ m/^email\=(.+)$/){
	$email_address = $1;
}
elsif($second =~ m/^email\=(.+)$/){
	$email_address = $1;
}
elsif ($third =~ m/^email\=(.+)$/){
	$email_address = $1;
}
elsif ($fourth =~ m/^email\=(.+)$/){
	$email_address= $1;
}
elsif ($fifth =~ m/^email\=(.+)$/){
	$email_address= $1;
}
elsif ($sixth =~ m/^email\=(.+)$/){
	$email_address = $1;
}
elsif ($seventh =~ m/^email\=(.+)$/){
	$email_address= $1;
}

if($first =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}
elsif($second =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}
elsif ($third =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}
elsif ($fourth =~ m/^jobFunction\=(.+)$/){
	$job_function= $1;
}
elsif ($fifth =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}
elsif ($sixth =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}
elsif ($seventh =~ m/^jobFunction\=(.+)$/){
	$job_function = $1;
}

if($first =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif($second =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif ($third =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif ($fourth =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif ($fifth =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif ($sixth =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}
elsif ($seventh =~ m/^companySize\=(.+)$/){
	$company_size = $1;
}

if($first =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif($second =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif ($third =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif ($fourth =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif ($fifth =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif ($sixth =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}
elsif ($seventh =~ m/^companyBuisness\=(.+)$/){
	$company_buisness = $1;
}


if($first =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif($second =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif ($third =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif ($fourth =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif ($fifth =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif ($sixth =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}
elsif ($seventh =~ m/^anonymous\=(.+)$/){
	$anonymousStr = $1;
}

if ($anonymousStr =~ "true") {
	$anonymous = 1;
}

open(USERS, "+<$allConsentedUsers ") || die "Can't open Log File: $!\n"; 
seek USERS, 0, 2;
print USERS "$firstName\t$lastName\t$email_address\t$job_function\t$company_size\t$company_buisness\n";
close USERS;

if($anonymous != 1){
	# give them the same id as before
	my($old) = &checkExistance($firstName, $lastName, $email_address);
	if($old == -1){
		$uid = &getNewUID($firstName, $lastName, $email_address);
	}
	else{
		$uid = $old;
	}
}
else
{
	$uid = &getNewUID("anonymous", "anonymous", "anonymous");
}

if($uid != -1)
{
	print "Content-type: text/plain", "\n";
	print "Status: 200 OK", "\n\n";
	print "UID: $uid" . "\n";
	exit;
}
else
{
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented", "\n\n";
	print "COULD NOT GET UID" . "\n";
	exit;
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

sub getNewUID {
	my($firstName, $lastName, $email_address) = @_;

	open(LOCKFILE, $lockfileName);
    	flock(LOCKFILE, LOCK_EX);

	open(NEXTUID, "<$nextUidFile") || die "Can't open Log File: $!\n"; 
	my($uid) = -1;
	my(@lines) = <NEXTUID>;
	my($line) = "";
	foreach $line (@lines)
	{
		if($line =~ m/^(\d+)$/)
		{
			$uid = $1;		   
			last;
		}
	}		
	close NEXTUID;

	my($nextUid) = $uid + 17;
	open(NEXTUID, ">$nextUidFile") || die "Can't open Log File: $!\n"; 
	print NEXTUID $nextUid;		
	close NEXTUID;
	    	

	open(UIDMAP, "+<$uidMapFile") || die "Can't open Log File: $!\n"; 	
	seek UIDMAP, 0, 2;
	print UIDMAP "$uid\t$firstName\t$lastName\t$email_address\n";	
	close UIDMAP;

	flock(LOCKFILE, LOCK_UN);
	close LOCKFILE;
	return $uid;

}
#################################################################### 

#################################################################### 

sub checkExistance {
	my($firstName, $lastName, $email_address) = @_;
	my($uid) = -1;

	open(LOCKFILE, $lockfileName);
    	flock(LOCKFILE, LOCK_EX);
    	
	open(UIDMAP, $uidMapFile) || die "Can't open Log File: $!\n"; 
	my(@lines) = <UIDMAP>;
	my($line) = "";
	foreach $line (@lines)
	{
		if($line =~ m/^(\d+)\t$firstName\t$lastName\t$email_address$/)
		{
			$uid = $1;
			last;
		}
	}
	close UIDMAP;

	flock(LOCKFILE, LOCK_UN);
	close LOCKFILE;
	return $uid;
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
