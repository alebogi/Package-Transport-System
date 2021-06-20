CREATE PROCEDURE spGrantRequest
	@username varchar(100)
AS
BEGIN
	declare @vehicle varchar(100)
	declare @tmpUsername varchar(100)

	set @tmpUsername= (select Username from CourierRequest where Username=@username)

	if(@tmpUsername = @username)
	begin
		set @vehicle= (select LicencePlateNum from CourierRequest where Username=@username)

		delete from CourierRequest where Username=@username

		insert into Courier(CourierUsername, LicencePlateNum) values (@username, @vehicle)
	end
	
END
GO
