-- phpMyAdmin SQL Dump
-- version 4.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 23, 2017 at 01:12 ص
-- Server version: 10.1.16-MariaDB
-- PHP Version: 7.0.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `SearchEngine`
--

-- --------------------------------------------------------

--
-- Table structure for table `Word`
--

CREATE TABLE `Word` (
  `WordID` int(11) NOT NULL,
  `Name` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `Word` (`WordID`, `Name`) VALUES
(2, 'Mosa');

--
-- Indexes for table `Word`
--
ALTER TABLE `Word`
  ADD PRIMARY KEY (`WordID`);
--
-- AUTO_INCREMENT for table `Word`
--
ALTER TABLE `Word`
  MODIFY `WordID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";




CREATE TABLE `URL` (
  `ID` int(11) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Word_Pri` int(11) NOT NULL COMMENT '(Priority+freq)*10^n',
  `U_WordID` int(11) NOT NULL,
  `PageRabk` int(11) NOT NULL COMMENT ' int Mo2ktn'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



INSERT INTO `URL` (`ID`, `Name`, `Word_Pri`, `U_WordID`, `PageRabk`) VALUES
(2, 'www.dsls.ssa', 32342, 2, 232);



--
-- Indexes for table `URL`
--
ALTER TABLE `URL`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `Word_ID` (`U_WordID`);


ALTER TABLE `URL`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `URL`
--
ALTER TABLE `URL`
  ADD CONSTRAINT `FK_Word_URL` FOREIGN KEY (`U_WordID`) REFERENCES `Word` (`WordID`) ON UPDATE CASCADE;


