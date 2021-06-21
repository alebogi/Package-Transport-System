CREATE TRIGGER TR_TransportOffer_DeleteAllOffersForPackage
   ON  Package
   AFTER UPDATE
AS 
BEGIN
	declare @IdPckg int, @IdOffer int, @StatusPckg int
	declare @kursor cursor

	set @StatusPckg = (select Status from inserted)
	if(@StatusPckg != 1)
		return

	set @IdPckg = (select IdPckg from inserted)

	set @kursor = cursor for
	select IdOffer
	from TransportOffer

	open @kursor

	fetch next from @kursor
	into @IdOffer

	while @@FETCH_STATUS = 0
	begin
		 delete from TransportOffer 
		 where IdOffer=@IdOffer

		fetch next from @kursor
		into @IdOffer
	end

	close @kursor
	deallocate @kursor

END
GO
